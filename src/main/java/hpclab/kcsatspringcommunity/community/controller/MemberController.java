package hpclab.kcsatspringcommunity.community.controller;

import hpclab.kcsatspringcommunity.UserService;
import hpclab.kcsatspringcommunity.community.dto.MemberSignInForm;
import hpclab.kcsatspringcommunity.community.dto.MemberSignUpForm;
import hpclab.kcsatspringcommunity.community.service.MemberService;
import hpclab.kcsatspringcommunity.exception.ApiResponse;
import hpclab.kcsatspringcommunity.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static hpclab.kcsatspringcommunity.exception.SuccessCode.LOGIN_SUCCESS;

/**
 * 회원 정보를 관리하는 컨트롤러 클래스입니다.
 * <p>기능 목록</p>
 * <ul>
 *     <li>회원 가입</li>
 *     <li>로그인</li>
 * </ul>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserService userService;

    /**
     * 회원가입하는 메서드입니다.
     * 자세한 포맷 양식은 Member 엔티티 객체 참조.
     *
     * @param memberSignUpForm 회원가입을 위한 email 아이디, 비밀번호 양식입니다.
     * @return 회원가입이 정상적으로 이루어지면 ok, 그렇지 않으면 BAD_REQUEST 반환.
     */
    @PostMapping("/api/community/open/signUp")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid MemberSignUpForm memberSignUpForm) {
        memberService.signUp(memberSignUpForm);

        return ResponseEntity.ok(new ApiResponse<>(true, null, LOGIN_SUCCESS.getCode(), LOGIN_SUCCESS.getMessage()));
    }

    /**
     * 로그인하는 메서드입니다.
     *
     * @param form 로그인을 위한 email 아이디, 비밀번호 양식입니다.
     * @return 로그인에 성공하면 JWT 토큰을 반환하고, 실패하면 UNAUTHORIZED를 반환합니다.
     */
    @PostMapping("/api/community/open/signIn")
    public ResponseEntity<String> signIn(@RequestBody MemberSignInForm form) {
        // 로그인 시도 및 토큰 발급
        String token = userService.login(form);

        // JWT 토큰을 응답으로 반환
        return ResponseEntity.ok(token);
    }

//    @PostMapping("/api/community/logout")
//    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
//        String jwtToken = token.split(" ")[1];
//        long expiration = jwtUtil.getExpiration(jwtToken);
//
//        // JWT 블랙리스트에 추가
//        userService.logout(jwtToken, expiration);
//
//        return ResponseEntity.ok("로그아웃 되었습니다.");
//    }
}
