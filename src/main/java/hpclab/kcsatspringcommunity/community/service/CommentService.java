package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.CommentWriteForm;

import java.util.List;

public interface CommentService {

    Long writeComment(CommentWriteForm commentWriteForm, Long id, String email);

    List<CommentResponseForm> getHotComments(Long pId);

    void deleteComment(Long id);

    String setCommentCount(Long commentId);

    String increaseCommentCount(Long commentId, String userEmail);

    String decreaseCommentCount(Long commentId, String userEmail);

    String getIncreaseCommentCount(Long commentId);

    String getDecreaseCommentCount(Long commentId);
}