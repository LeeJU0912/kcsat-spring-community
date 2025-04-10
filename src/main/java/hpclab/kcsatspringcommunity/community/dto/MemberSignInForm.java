package hpclab.kcsatspringcommunity.community.dto;

import lombok.Data;

/**
 * 로그인 Form DTO 클래스입니다.
 */
@Data
public class MemberSignInForm {
    private String userEmail;
    private String password;
}
