package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.RedisKeyUtil;
import hpclab.kcsatspringcommunity.myBook.domain.Book;
import hpclab.kcsatspringcommunity.myBook.domain.BookQuestion;
import hpclab.kcsatspringcommunity.myBook.repository.BookQuestionRepository;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문제를 나만의 문제집에 저장하는 로직을 구현한 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookQuestionServiceImpl implements BookQuestionService {

    private final BookQuestionRepository bookQuestionRepository;

    private final BookService bookService;
    private final QuestionService questionService;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    @Override
    public Long saveFirstQuestion(Question question, String userEmail) {

        questionService.saveQuestion(question);
        Book book = bookService.findBook(userEmail);

        bookQuestionRepository.save(new BookQuestion(book, question));

        return book.getId();
    }

    @Transactional
    @Override
    public Long saveQuestion(Long qId, String userEmail) {

        Question question = questionService.getQuestion(qId);
        Book book = bookService.findBook(userEmail);

        if (redisTemplate.opsForValue().get(RedisKeyUtil.questionSavedCheck(userEmail, qId)) == null) {
            redisTemplate.opsForValue().set(RedisKeyUtil.questionSavedCheck(userEmail, qId), "1");
            question.upShareCounter();
            bookQuestionRepository.save(new BookQuestion(book, question));
        }

        return book.getId();
    }
}