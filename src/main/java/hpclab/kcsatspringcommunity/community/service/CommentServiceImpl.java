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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Long writeComment(CommentWriteForm commentWriteForm, Long id, String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 게시물입니다."));

        Comment comment = Comment.builder()
                .content(commentWriteForm.getContent())
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);

        return comment.getCId();
    }

    @Override
    public List<CommentResponseForm> getHotComments(Long pId) {
        List<CommentResponseForm> hotComments = new ArrayList<>();

        Post post = postRepository.findByIdWithComments(pId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        List<CommentsSort> commentsSort = new ArrayList<>();
        for (Comment comment : post.getComment()) {
            String upVote = redisTemplate.opsForValue().get("comment:" + comment.getCId() + ":upVote");
            String downVote = redisTemplate.opsForValue().get("comment:" + comment.getCId() + ":downVote");

            long calc = Long.parseLong(upVote) - Long.parseLong(downVote);

            if (calc >= 2) {
                commentsSort.add(new CommentsSort(calc, pId, comment));
            }
        }

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

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public String setCommentCount(Long commentId) {

        String upVote = "comment:" + commentId + ":upVote";
        String downVote = "comment:" + commentId + ":downVote";
        redisTemplate.opsForValue().set(upVote, "0");
        redisTemplate.opsForValue().set(downVote, "0");

        return redisTemplate.opsForValue().get(upVote);
    }

    @Override
    public String increaseCommentCount(Long commentId, String userEmail) {

        String upVote = "comment:" + commentId + ":upVote";
        String user = "comment:" + commentId + ":user:" + userEmail;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(user))) {
            redisTemplate.opsForValue().increment(upVote);
            redisTemplate.opsForValue().set(user, "1");
        }

        return redisTemplate.opsForValue().get(upVote);
    }


    @Override
    public String decreaseCommentCount(Long commentId, String userEmail) {

        String downVote = "comment:" + commentId + ":downVote";
        String user = "comment:" + commentId + ":user:" + userEmail;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(user))) {
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