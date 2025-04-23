package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.domain.Comment;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.CommentWriteForm;
import hpclab.kcsatspringcommunity.community.repository.CommentRepository;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    @Override
    public Long writeComment(CommentWriteForm commentWriteForm, Long pId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Post post = postRepository.findById(pId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 게시물입니다."));

        Comment comment = Comment.builder()
                .content(commentWriteForm.getContent())
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);

        return comment.getCId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseForm> getHotComments(Long pId) {
        List<CommentResponseForm> hotComments = new ArrayList<>();

        Post post = postRepository.findByIdWithComments(pId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        List<CommentsSort> commentsSort = new ArrayList<>();
        post.getComment().forEach(comment -> {
            String upVote = redisTemplate.opsForValue().get("comment:" + comment.getCId() + ":upVote");
            String downVote = redisTemplate.opsForValue().get("comment:" + comment.getCId() + ":downVote");

            try {
                long calc = Long.parseLong(upVote) - Long.parseLong(downVote);

                if (calc >= 2) {
                    commentsSort.add(new CommentsSort(calc, pId, comment));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        });

        Collections.sort(commentsSort);

        for (int i = 0; i < min(commentsSort.size(), 3); i++) {
            hotComments.add(
                    CommentResponseForm.builder()
                            .comment(commentsSort.get(i).comment)
                            .build()
            );
        }

        return hotComments;
    }

    private static class CommentsSort implements Comparable<CommentsSort> {
        Long counter;
        Long cid;

        Comment comment;

        public CommentsSort(Long counter, Long cid, Comment comment) {
            this.counter = counter;
            this.cid = cid;
            this.comment = comment;
        }

        @Override
        public int compareTo(CommentsSort o) {

            return (int) (o.counter - counter);
        }
    }

    @Transactional
    @Override
    public void deleteComment(Long cId) {
        commentRepository.deleteById(cId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkCommentWriter(String email, Long cId) {
        Comment comment = commentRepository.findCommentWithMember(cId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 댓글입니다."));

        return comment.getMember().getEmail().equals(email);
    }

    @Override
    public String setCommentCount(Long cId) {

        String upVote = "comment:" + cId + ":upVote";
        String downVote = "comment:" + cId + ":downVote";
        redisTemplate.opsForValue().set(upVote, "0");
        redisTemplate.opsForValue().set(downVote, "0");

        return redisTemplate.opsForValue().get(upVote);
    }

    @Override
    public String increaseCommentCount(Long cId, String userEmail) {

        String upVote = "comment:" + cId + ":upVote";
        String user = "comment:" + cId + ":user:" + userEmail;

        if (!redisTemplate.hasKey(user)) {
            redisTemplate.opsForValue().increment(upVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(upVote);
    }


    @Override
    public String decreaseCommentCount(Long commentId, String userEmail) {

        String downVote = "comment:" + commentId + ":downVote";
        String user = "comment:" + commentId + ":user:" + userEmail;

        if (!redisTemplate.hasKey(user)) {
            redisTemplate.opsForValue().increment(downVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(downVote);
    }

    @Override
    public String getIncreaseCommentCount(Long commentId) {
        return redisTemplate.opsForValue().get("comment:" + commentId + ":upVote");
    }

    @Override
    public String getDecreaseCommentCount(Long commentId) {
        return redisTemplate.opsForValue().get("comment:" + commentId + ":downVote");
    }
}