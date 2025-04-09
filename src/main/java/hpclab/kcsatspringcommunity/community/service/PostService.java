package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.dto.PostDetailForm;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostWriteForm;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Long savePost(PostWriteForm postWriteForm, String email);

    boolean saveQuestionFromPost(Long qId, String userEmail);

    Page<PostResponseForm> getPostList(Pageable pageable);

    Page<PostResponseForm> getFindPostList(Pageable pageable, String keyword, QuestionType type);

    Page<PostResponseForm> getHotPostList(Pageable pageable);

    Page<PostResponseForm> getFindHotPostList(Pageable pageable, String keyword, QuestionType type);

    PostDetailForm getPost(Long postId);

    PostDetailForm updatePost(Long postId, PostWriteForm postWriteForm);

    void removePost(Long id);

    String setPostCount(Long postId);

    String increasePostViewCount(Long postId, String userEmail);

    String getPostViewCount(Long postId);

    String increasePostVoteCount(Long postId, String userEmail);

    String decreasePostVoteCount(Long postId, String userEmail);

    String getIncreasePostVoteCount(Long postId);

    String getDecreasePostVoteCount(Long postId);

    void resetPostView();
}
