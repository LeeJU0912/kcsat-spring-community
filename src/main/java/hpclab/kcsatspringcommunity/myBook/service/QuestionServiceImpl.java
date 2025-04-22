package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 문제 관련 로직을 구현한 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionJPARepository questionJPARepository;

    @Override
    public Question getQuestion(Long qId) {
        return questionJPARepository.findWithChoicesById(qId).orElseThrow(() -> new IllegalArgumentException("Question not found"));
    }

    @Override
    public Long saveQuestion(Question question) {
        return questionJPARepository.save(question).getId();
    }
}
