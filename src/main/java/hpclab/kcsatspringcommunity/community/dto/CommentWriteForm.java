package hpclab.kcsatspringcommunity.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글에 댓글 작성 시 사용자가 제출할 때 받는 Form DTO입니다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentWriteForm {

    /**
     * 댓글 본문 내용
     */
    private String content;
}
