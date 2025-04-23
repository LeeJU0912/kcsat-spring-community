package hpclab.kcsatspringcommunity.admin.controller;

import hpclab.kcsatspringcommunity.JWTUtil;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestRequestForm;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestResponseForm;
import hpclab.kcsatspringcommunity.admin.service.UserRequestService;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.dto.CommentResponseForm;
import hpclab.kcsatspringcommunity.community.dto.MemberDetailsResponseForm;
import hpclab.kcsatspringcommunity.community.dto.MemberResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.service.MemberService;
import hpclab.kcsatspringcommunity.exception.ApiResponse;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hpclab.kcsatspringcommunity.JWTUtil.AUTHORIZATION;
import static hpclab.kcsatspringcommunity.JWTUtil.USER_EMAIL;

/**
 * <p>이 클래스는 관리자 페이지 컨트롤러 클래스입니다.</p>
 * <p>관리자 전용 기능을 구현한 페이지입니다.</p>
 * <p><b>관리자 권한 계정을 필수로 요구합니다. 이외의 계정에 대해서는 접근이 불가능합니다.</b></p>
 *
 * <p>기능 목록</p>
 * <ul>
 *     <li>회원 목록 조회</li>
 *     <li>회원 세부 정보 조회</li>
 *     <li>회원 작성 글 / 댓글 목록 조회</li>
 *     <li>제작 문제 신고 사항 조회</li>
 *     <li>문제 건의 사항 조회</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final UserRequestService userRequestService;
    private final QuestionService questionService;
    private final JWTUtil jwtUtil;

    /**
     * 모든 회원 요구 사항을 불러옵니다.
     * RequestType 구분 없이 모든 요구 사항을 한번에 불러옵니다.
     *
     * @return 회원 건의사항 목록
     */
    @GetMapping("/api/community/admin/requests")
    public ResponseEntity<ApiResponse<List<UserRequestResponseForm>>> getUserRequests() {
        return ResponseEntity.ok(new ApiResponse<>(true, userRequestService.getUserRequests(), null, null));
    }

    /**
     * 회원 목록을 불러옵니다.
     *
     * @return 회원 목록(ID)
     */
    @GetMapping("/api/community/admin/members")
    public ResponseEntity<ApiResponse<List<MemberResponseForm>>> getMemberList() {
        return ResponseEntity.ok(new ApiResponse<>(true, memberService.findMembers(), null, null));
    }

    /**
     * 회원 목록에서 특정 회원을 누르면, 회원 세부 정보를 불러옵니다.
     *
     * @param mId 회원 아이디
     * @return 회원 세부 정보
     */
    @GetMapping("/api/community/admin/members/{mId}")
    public ResponseEntity<ApiResponse<MemberDetailsResponseForm>> getMemberDetail(@PathVariable Long mId) {
        Member member = memberService.findMemberById(mId);
        return ResponseEntity.ok(new ApiResponse<>(true, new MemberDetailsResponseForm(member), null, null));
    }

    /**
     * 회원이 쓴 게시글 목록을 조회합니다.
     *
     * @param mId 회원 아이디
     * @return 회원 작성 게시글 목록
     */
    @GetMapping("/api/community/admin/members/{mId}/posts")
    public ResponseEntity<ApiResponse<List<PostResponseForm>>> getMemberDetailPosts(@PathVariable Long mId) {
        Member member = memberService.findMemberById(mId);
        List<PostResponseForm> posts = member.getPosts().stream()
                .map(x -> PostResponseForm.builder().post(x).build())
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, posts, null, null));
    }

    /**
     * 회원이 쓴 댓글 목록을 조회합니다.
     *
     * @param mId 회원 아이디
     * @return 회원 작성 댓글 목록
     */
    @GetMapping("/api/community/admin/members/{mId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseForm>>> memberDetailComments(@PathVariable Long mId) {
        Member member = memberService.findMemberById(mId);
        List<CommentResponseForm> comments = member.getComments().stream()
                .map(x -> CommentResponseForm.builder().comment(x).build())
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, comments, null, null));
    }

    /**
     * 문제 제작 후, 오류가 있는 문제에 대해 신고하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 오류 문제 세부 사항
     * @return 보낸 문제에 대한 신고자, 문제 세부 사항 등 결과 객체
     */
    @PostMapping("/api/community/result/junk")
    public ResponseEntity<ApiResponse<UserRequestResponseForm>> filterQuestion(@RequestHeader(AUTHORIZATION) String token, @RequestBody QuestionDto form) {
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
    @PostMapping("/api/community/improving")
    public ResponseEntity<ApiResponse<UserRequestResponseForm>> requestImproving(@RequestHeader(AUTHORIZATION) String token, @RequestBody UserRequestRequestForm form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        UserRequestResponseForm userRequestResponse = userRequestService.updateUserRequestForm(userRequestService.getImprovingForm(form, userEmail), userEmail);

        return ResponseEntity.ok(new ApiResponse<>(true, userRequestResponse, null, null));
    }
}