package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.RedisKeyUtil;
import hpclab.kcsatspringcommunity.community.domain.Comment;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.community.dto.CommentDetailForm;
import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.CommentWriteForm;
import hpclab.kcsatspringcommunity.community.repository.CommentRepository;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.min;

/**
 * 댓글 처리 로직을 담당하는 메서드입니다.
 * <p>기능 목록</p>
 * <ul>
 *     <li>댓글 작성</li>
 *     <li>댓글 삭제</li>
 *     <li>게시글 인기 댓글 목록 조회(일반 댓글은 게시글과 묶음 조회)</li>
 *     <li>댓글 추천/비추천수 조회</li>
 *     <li>댓글 추천, 비추천</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final MemberService memberService;

    private final CommentRepository commentRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    @Override
    public Long writeComment(CommentWriteForm commentWriteForm, Long pId, String email) {
        Member member = memberService.findMemberByEmail(email);

        String hexString = Integer.toHexString(commentWriteForm.getContent().hashCode());

        String redisKey = RedisKeyUtil.commentIdemCheck(member.getMID(), hexString);

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(redisKey, "locked", Duration.ofMinutes(1));

        if (Boolean.FALSE.equals(locked)) {
            throw new ApiException(ErrorCode.DUPLICATE_COMMENT);
        }

        Comment comment = Comment.builder()
                .content(commentWriteForm.getContent())
                .member(member)
                .pId(pId)
                .build();

        commentRepository.save(comment);

        return comment.getCId();
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDetailForm getAllComments(Long pId) {

        List<Comment> comments = commentRepository.findByPId(pId);
        List<CommentResponseForm> hotComments = getHotComments(comments);
        List<CommentResponseForm> normalComments = new ArrayList<>();
        comments.forEach(comment -> normalComments.add(new CommentResponseForm(comment)));

        List<String> hotCommentsUpVoteCounter = new ArrayList<>();
        List<String> hotCommentsDownVoteCounter = new ArrayList<>();
        List<String> commentsUpVoteCounter = new ArrayList<>();
        List<String> commentsDownVoteCounter = new ArrayList<>();

        hotComments.forEach(comment -> {
            String commentUpVoteCount = getIncreaseCommentCount(comment.getCId());
            hotCommentsUpVoteCounter.add(commentUpVoteCount);

            String commentDownVoteCount = getDecreaseCommentCount(comment.getCId());
            hotCommentsDownVoteCounter.add(commentDownVoteCount);
        });

        normalComments.forEach(comment -> {
            String commentUpVoteCount = getIncreaseCommentCount(comment.getCId());
            commentsUpVoteCounter.add(commentUpVoteCount);

            String commentDownVoteCount = getDecreaseCommentCount(comment.getCId());
            commentsDownVoteCounter.add(commentDownVoteCount);
        });

        return new CommentDetailForm(
                hotComments,
                hotCommentsUpVoteCounter,
                hotCommentsDownVoteCounter,
                normalComments,
                commentsUpVoteCounter,
                commentsDownVoteCounter
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseForm> getHotComments(List<Comment> comments) {
        List<CommentResponseForm> hotComments = new ArrayList<>();

        List<CommentsSort> commentsSort = new ArrayList<>();
        comments.forEach(comment -> {
            String upVote = redisTemplate.opsForValue().get(RedisKeyUtil.commentUpVote(comment.getCId()));
            String downVote = redisTemplate.opsForValue().get(RedisKeyUtil.commentDownVote(comment.getCId()));

            try {
                long calc = Long.parseLong(upVote) - Long.parseLong(downVote);

                if (calc >= 2) {
                    commentsSort.add(new CommentsSort(calc, comment));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        });

        commentsSort.sort(Comparator.comparing(CommentsSort::getCounter).reversed());

        for (int i = 0; i < min(commentsSort.size(), 3); i++) {
            hotComments.add(
                    CommentResponseForm.builder()
                            .comment(commentsSort.get(i).comment)
                            .build()
            );
        }

        return hotComments;
    }

    private class CommentsSort {
        Long counter;
        Comment comment;

        public CommentsSort(Long counter, Comment comment) {
            this.counter = counter;
            this.comment = comment;
        }

        Long getCounter() {
            return counter;
        }
    }

    @Transactional
    @Override
    public void deleteComment(Long cId) {
        commentRepository.deleteById(cId);
    }

    @Transactional(readOnly = true)
    @Override
    public void checkCommentWriter(String email, Long cId) {
        Comment comment = commentRepository.findCommentWithMember(cId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getMember().getEmail().equals(email)) {
            throw new ApiException(ErrorCode.USER_VERIFICATION_FAILED);
        }
    }

    @Override
    public String setCommentCount(Long cId) {

        String upVote = RedisKeyUtil.commentUpVote(cId);
        String downVote = RedisKeyUtil.commentDownVote(cId);

        redisTemplate.opsForValue().set(upVote, "0");
        redisTemplate.opsForValue().set(downVote, "0");

        return redisTemplate.opsForValue().get(upVote);
    }

    @Override
    public String increaseCommentCount(Long cId, String userEmail) {

        String upVoteKey = RedisKeyUtil.commentUpVote(cId);
        String userVoteKey = RedisKeyUtil.commentUserCheck(cId, userEmail);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(userVoteKey, "1", Duration.ofHours(24));

        if (Boolean.TRUE.equals(success)) {
            redisTemplate.opsForValue().increment(upVoteKey);
        }

        return redisTemplate.opsForValue().get(upVoteKey);
    }


    @Override
    public String decreaseCommentCount(Long commentId, String userEmail) {

        String downVoteKey = RedisKeyUtil.commentDownVote(commentId);
        String userVoteKey = RedisKeyUtil.commentUserCheck(commentId, userEmail);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(userVoteKey, "1", Duration.ofHours(24));

        if (Boolean.TRUE.equals(success)) {
            redisTemplate.opsForValue().increment(downVoteKey);
        }

        return redisTemplate.opsForValue().get(downVoteKey);
    }

    @Override
    public String getIncreaseCommentCount(Long commentId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.commentUpVote(commentId));
    }

    @Override
    public String getDecreaseCommentCount(Long commentId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.commentDownVote(commentId));
    }
}