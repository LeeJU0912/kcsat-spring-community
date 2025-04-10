package hpclab.kcsatspringcommunity.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원이 게시글을 작성하면 값을 저장하는 DTO 클래스입니다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostWriteForm {

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 본문
     */
    private String content;

    /**
     * 게시글에 첨부한 문제 ID
     */
    private Long qId;
}