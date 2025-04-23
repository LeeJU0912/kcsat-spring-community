package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.domain.Comment;
import hpclab.kcsatspringcommunity.community.dto.CommentDetailForm;
import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.CommentWriteForm;

import java.util.List;

/**
 * 게시글에서 댓글에 대한 상호작용을 처리하는 로직을 정의한 인터페이스입니다.
 */
public interface CommentService {

    /**
     * 게시글을 작성하는 메서드입니다.
     *
     * @param commentWriteForm 게시글 작성 Form DTO 객체
     * @param pId 댓글이 달릴 게시글 ID
     * @param email 댓글을 작성하는 사용자 email
     * @return 저장된 댓글 ID를 반환합니다.
     */
    Long writeComment(CommentWriteForm commentWriteForm, Long pId, String email);


    /**
     * 게시글에 작성된 모든 댓글을 불러오는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글의 인기 댓글 3개와 모든 댓글들을 목록으로 담아 반환합니다.
     */
    CommentDetailForm getAllComments(Long pId);

    /**
     * 게시글에 작성된 인기 게시글을 불러오는 메서드입니다.
     *
     * @param comments 게시글 ID
     * @return 게시글에 달린 인기 댓글들을 목록으로 담아 반환합니다.
     */
    List<CommentResponseForm> getHotComments(List<Comment> comments);

    /**
     * 댓글을 삭제하는 메서드입니다.
     *
     * @param cId 삭제할 댓글 ID
     */
    void deleteComment(Long cId);

    /**
     * 댓글 작성자와 현재 요청한 회원 정보가 일치하는지 판별하는 메서드입니다.
     *
     * @param email 현재 요청한 회원 email 아이디
     * @param cId 댓글 ID
     */
    void checkCommentWriter(String email, Long cId);

    /**
     * 댓글 최초 작성 시 추천, 비추천 카운트를 0으로 세팅하는 메서드입니다.
     *
     * @param cId 댓글 ID
     * @return 추천 카운트를 반환합니다.
     */
    String setCommentCount(Long cId);

    /**
     * cId에 해당하는 댓글 추천을 1 올리는 메서드입니다.
     * 회원마다 하루에 2번 이상 추천/비추천이 불가능합니다.
     *
     * @param cId 댓글 ID
     * @param userEmail 회원 email 아이디
     * @return 추천 카운트를 반환합니다.
     */
    String increaseCommentCount(Long cId, String userEmail);

    /**
     * cId에 해당하는 댓글 비추천을 1 올리는 메서드입니다.
     * 회원마다 하루에 2번 이상 추천/비추천이 불가능합니다.
     *
     * @param cId 댓글 ID
     * @param userEmail 회원 email 아이디
     * @return 비추천 카운트를 반환합니다.
     */
    String decreaseCommentCount(Long cId, String userEmail);

    /**
     * cId에 해당하는 댓글의 추천수를 가져오는 메서드입니다.
     *
     * @param cId 댓글 ID
     * @return 추천 카운트를 반환합니다.
     */
    String getIncreaseCommentCount(Long cId);

    /**
     * cId에 해당하는 댓글의 비추천수를 가져오는 메서드입니다.
     *
     * @param cId 댓글 ID
     * @return 비추천 카운트를 반환합니다.
     */
    String getDecreaseCommentCount(Long cId);
}