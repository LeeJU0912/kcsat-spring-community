package hpclab.kcsatspringcommunity.community.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 게시글 상세 정보 변환 DTO 클래스입니다.
 */
@Data
@Builder
public class PostDetailForm {

    /**
     * 게시글 상세 정보. 자세한 사항은 PostResponseForm 참조.
     */
    private PostResponseForm post;

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
