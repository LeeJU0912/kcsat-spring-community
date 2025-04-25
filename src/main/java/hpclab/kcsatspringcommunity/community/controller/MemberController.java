package hpclab.kcsatspringcommunity.community.controller;

import hpclab.kcsatspringcommunity.util.JWTUtil;
import hpclab.kcsatspringcommunity.UserService;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestRequestForm;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestResponseForm;
import hpclab.kcsatspringcommunity.admin.service.UserRequestService;
import hpclab.kcsatspringcommunity.community.dto.MemberAuthResponseForm;
import hpclab.kcsatspringcommunity.community.dto.MemberSignInForm;
import hpclab.kcsatspringcommunity.community.dto.MemberSignUpForm;
import hpclab.kcsatspringcommunity.community.service.MemberService;
import hpclab.kcsatspringcommunity.exception.ApiResponse;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionDto;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hpclab.kcsatspringcommunity.util.JWTUtil.USER_EMAIL;
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
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class MemberController {

    private final QuestionService questionService;
    private final UserRequestService userRequestService;
    private final MemberService memberService;
    private final UserService userService;

    private final JWTUtil jwtUtil;

    /**
     * 회원가입하는 메서드입니다.
     * 자세한 포맷 양식은 Member 엔티티 객체 참조.
     *
     * @param memberSignUpForm 회원가입을 위한 email 아이디, 비밀번호 양식입니다.
     * @return 회원가입이 정상적으로 이루어지면 ok, 그렇지 않으면 BAD_REQUEST 반환.
     */
    @PostMapping("/open/signUp")
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
    @PostMapping("/internal/signIn")
    public ResponseEntity<ApiResponse<MemberAuthResponseForm>> signIn(@RequestBody @Valid MemberSignInForm form) {
        // 로그인 시도 및 토큰 발급
        MemberAuthResponseForm member = userService.login(form);

        // JWT 토큰을 응답으로 반환
        return ResponseEntity.ok(new ApiResponse<>(true, member, null, null));
    }


    /**
     * 문제 제작 후, 오류가 있는 문제에 대해 신고하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 오류 문제 세부 사항
     * @return 보낸 문제에 대한 신고자, 문제 세부 사항 등 결과 객체
     */
    @PostMapping("/junk")
    public ResponseEntity<ApiResponse<UserRequestResponseForm>> filterQuestion(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody QuestionDto form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        Question question = Question
                .builder()
                .type(form.getQuestionType())
                .title(form.getTitle())
                .mainText(form.getMainText())
                .choices(form.getChoices().stream().map(Choice::new).toList())
                .answer(form.getAnswer())
                .translation(form.getTranslation())
                .explanation(form.getExplanation())
                .shareCounter(0L)
                .build();

        Long qId = questionService.saveQuestion(question);

        UserRequestResponseForm userRequestResponse = userRequestService.updateUserRequestForm(userRequestService.getQuestionErrorForm(qId, userEmail), userEmail);

        return ResponseEntity.ok(new ApiResponse<>(true, userRequestResponse, null, null));
    }

    /**
     * 회원 요청 사항을 요구하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 오류 문제 세부 사항
     * @return 보낸 문제에 대한 신고자, 문제 세부 사항 등 결과 객체
     */
    @PostMapping("/improving")
    public ResponseEntity<ApiResponse<UserRequestResponseForm>> requestImproving(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody UserRequestRequestForm form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        UserRequestResponseForm userRequestResponse = userRequestService.updateUserRequestForm(userRequestService.getImprovingForm(form, userEmail), userEmail);

        return ResponseEntity.ok(new ApiResponse<>(true, userRequestResponse, null, null));
    }
}
