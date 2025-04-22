package hpclab.kcsatspringcommunity.myBook.controller;

import hpclab.kcsatspringcommunity.JWTUtil;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.service.BookQuestionService;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionDetailsDto;
import hpclab.kcsatspringcommunity.question.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 나만의 문제를 저장하는 MyBook 관련 컨트롤러 메서드입니다.
 */
@Slf4j
@RestController
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
    @GetMapping("/api/community/myBook")
    public ResponseEntity<BookResponseForm> myQuestion(@RequestHeader("Authorization") String token) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        try {
            return ResponseEntity.ok(new BookResponseForm(bookService.findBook(userEmail)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 회원 커뮤니티 게시판 게시글에 첨부된 문제를 저장하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 문제 세부 사항 DTO Form 객체
     * @return 문제 저장에 성공하면 OK로 응답합니다.
     */
    @PostMapping("/api/community/question/save")
    public ResponseEntity<String> saveQuestion(@RequestHeader("Authorization") String token, @RequestBody QuestionDto form) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

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

        try {
            return ResponseEntity.ok(bookQuestionService.saveFirstQuestion(question, userEmail).toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 문제에 대한 세부 항목을 조회하는 메서드입니다.
     *
     * @param qId 문제 ID
     * @return 문제 세부 사항을 반환합니다.
     */
    @GetMapping("/api/community/question")
    public ResponseEntity<QuestionDetailsDto> getQuestionById(@RequestParam Long qId) {
        try {
            Question question = questionService.getQuestion(qId);
            return ResponseEntity.ok(new QuestionDetailsDto(question));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}