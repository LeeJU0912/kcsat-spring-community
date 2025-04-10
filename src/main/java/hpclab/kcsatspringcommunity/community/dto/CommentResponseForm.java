package hpclab.kcsatspringcommunity.community.dto;

import hpclab.kcsatspringcommunity.community.domain.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 댓글 세부 내용을 반환하는 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
public class CommentResponseForm {

    /**
     * 최초 작성 시간
     */
    private LocalDateTime createdDateTime;

    /**
     * 댓글 수정 시간
     */
    private LocalDateTime modifiedDateTime;

    /**
     * 댓글 ID
     */
    private Long cId;

    /**
     * 댓글 본문 내용
     */
    private String content;

    /**
     * 댓글 작성자 별명
     */
    private String username;

    /**
     * 댓글 작성자 email 아이디
     */
    private String email;

    @Builder
    public CommentResponseForm(Comment comment) {
        this.createdDateTime = comment.getCreatedDate();
        this.modifiedDateTime = comment.getLastModifiedDate();
        this.cId = comment.getCId();
        this.content = comment.getContent();
        this.username = comment.getMember().getUsername();
        this.email = comment.getMember().getEmail();
    }
}
