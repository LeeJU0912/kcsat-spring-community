package hpclab.kcsatspringcommunity.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 회원가입 Form DTO 클래스입니다.
 */
@Data
@Builder
@AllArgsConstructor
public class MemberSignUpForm {

    /**
     * 회원 email 아이디
     */
    private String email;

    /**
     * 회원 별명
     */
    private String username;

    /**
     * 회원 비밀번호
     */
    private String password;
}
