package hpclab.kcsatspringcommunity.admin.dto;

import lombok.Data;

/**
 * 회원 요청 사항 본문 내용을 받는 Form DTO 클래스입니다.
 */
@Data
public class UserRequestRequestForm {

    /**
     * 회원 요청 사항 본문 내용이 들어갑니다.
     */
    private String content;
}
