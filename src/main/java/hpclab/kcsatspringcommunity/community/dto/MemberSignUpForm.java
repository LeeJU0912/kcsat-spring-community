package hpclab.kcsatspringcommunity.community.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일을 입력해주세요.")
    private String email;

    /**
     * 회원 별명
     */
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 12, message = "닉네임은 2 ~ 12자 사이로 입력해주세요.")
    private String username;

    /**
     * 회원 비밀번호
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 영문자와 숫자를 포함하여 8자 이상이어야 합니다.")
    private String password;
}