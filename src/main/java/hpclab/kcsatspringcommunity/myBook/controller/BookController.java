package hpclab.kcsatspringcommunity.myBook.controller;

import hpclab.kcsatspringcommunity.util.JWTUtil;
import hpclab.kcsatspringcommunity.exception.ApiResponse;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.service.BookQuestionService;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionDetailsDto;
import hpclab.kcsatspringcommunity.question.dto.QuestionDto;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hpclab.kcsatspringcommunity.util.JWTUtil.USER_EMAIL;

/**
 * 나만의 문제를 저장하는 MyBook 관련 컨트롤러 메서드입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookQuestionService bookQuestionService;
    private final QuestionService questionService;

    private final JWTUtil jwtUtil;

    /**
     * 나만의 문제가 저장된 MyBook을 조회하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @return MyBook 상세 정보를 반환합니다.
     */
    @GetMapping("/myBook")
    public ResponseEntity<ApiResponse<BookResponseForm>> myQuestion(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        return ResponseEntity.ok(new ApiResponse<>(true, new BookResponseForm(bookService.findBook(userEmail)), null, null));
    }

    /**
     * 회원 커뮤니티 게시판 게시글에 첨부된 문제를 저장하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 문제 세부 사항 DTO Form 객체
     * @return 문제 저장에 성공하면 OK로 응답합니다.
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveQuestion(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody QuestionDto form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        Question question = Question.builder()
                .type(form.getQuestionType())
                .title(form.getTitle())
                .mainText(form.getMainText())
                .answer(form.getAnswer())
                .translation(form.getTranslation())
                .explanation(form.getExplanation())
                .shareCounter(0L)
                .build();

        question.setChoices(form.getChoices().stream().map(Choice::new).toList());

        String bookID = bookQuestionService.saveFirstQuestion(question, userEmail).toString();

        return ResponseEntity.ok(new ApiResponse<>(true, bookID, null, null));
    }

    /**
     * 문제에 대한 세부 항목을 조회하는 메서드입니다.
     *
     * @param qId 문제 ID
     * @return 문제 세부 사항을 반환합니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<QuestionDetailsDto>> getQuestionById(@RequestParam Long qId) {
        Question question = questionService.getQuestion(qId);
        return ResponseEntity.ok(new ApiResponse<>(true, new QuestionDetailsDto(question), null, null));
    }
}