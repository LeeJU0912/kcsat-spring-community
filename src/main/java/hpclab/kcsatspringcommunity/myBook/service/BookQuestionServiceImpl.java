package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.myBook.domain.Book;
import hpclab.kcsatspringcommunity.myBook.domain.BookQuestion;
import hpclab.kcsatspringcommunity.myBook.repository.BookQuestionRepository;
import hpclab.kcsatspringcommunity.myBook.repository.BookRepository;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookQuestionServiceImpl implements BookQuestionService {
    private final BookRepository bookRepository;
    private final BookQuestionRepository bookQuestionRepository;
    private final QuestionJPARepository questionJPARepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Long saveFirstQuestion(Question question, String userEmail) {

        Question savedQuestion = questionJPARepository.save(question);

        log.info("savedQuestion : {}", savedQuestion);

        Book book = bookRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        bookQuestionRepository.save(new BookQuestion(book, savedQuestion));

        return book.getId();
    }

    @Override
    public Long saveQuestion(Long qId, String userEmail) {

        Question question = questionJPARepository.findById(qId).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 문제에여."));

        Book book = bookRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if (redisTemplate.opsForValue().get("question:" + userEmail + ":isSaved:" + qId) == null) {
            redisTemplate.opsForValue().set("question:" + userEmail + ":isSaved:" + qId, "1");
            question.upShareCounter();
            bookQuestionRepository.save(new BookQuestion(book, question));
        }

        return book.getId();
    }
}
