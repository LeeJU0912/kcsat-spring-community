package hpclab.kcsatspringcommunity.myBook.controller;

import hpclab.kcsatspringcommunity.JWTUtil;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.service.BookQuestionService;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import hpclab.kcsatspringcommunity.myBook.service.QuestionService;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionDetailsDto;
import hpclab.kcsatspringcommunity.question.dto.QuestionDto;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final BookService bookService;
    private final BookQuestionService bookQuestionService;
    private final QuestionService questionService;
    private final JWTUtil jwtUtil;


    // 마이북 반환
    @GetMapping("/api/community/myBook")
    public ResponseEntity<BookResponseForm> myQuestion(@RequestHeader("Authorization") String token) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();
        BookResponseForm book = bookService.findBook(userEmail);

        log.info(book.toString());

        return ResponseEntity.ok(book);
    }


    // 사용자 문제 저장 요청
    @PostMapping("/api/community/question/save")
    public ResponseEntity<String> saveQuestion(@RequestHeader("Authorization") String token, @RequestBody QuestionDto form) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();
        log.info(form.toString());

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

        bookQuestionService.saveFirstQuestion(question, userEmail);

        return ResponseEntity.ok("문제 저장 성공");
    }


    // 문제 상세 데이터 조회
    @GetMapping("/api/community/question")
    public ResponseEntity<QuestionDetailsDto> getQuestionById(@RequestParam Long qId) {
        Question question = questionService.getQuestion(qId);
        return ResponseEntity.ok(new QuestionDetailsDto(question));
    }
}