package hpclab.kcsatspringcommunity.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 댓글 상세 정보를 반환하는 DTO 클래스입니다. 주로 댓글 API 정보 반환에 쓰입니다.
 */
@Data
@AllArgsConstructor
public class CommentDetailForm {

    /**
     * 인기 댓글 모음
     */
    private List<CommentResponseForm> hotComments;

    /**
     * 인기 댓글 각각의 추천수
     */
    private List<String> hotCommentsUpVoteCounter;

    /**
     * 인기 댓글 각각의 비추천수
     */
    private List<String> hotCommentsDownVoteCounter;

    /**
     * 댓글 모음
     */
    private List<CommentResponseForm> comments;

    /**
     * 댓글 각각의 추천수
     */
    private List<String> commentsUpVoteCounter;

    /**
     * 댓글 각각의 비추천수
     */
    private List<String> commentsDownVoteCounter;
}
