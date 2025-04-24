package hpclab.kcsatspringcommunity.question.service;

import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 문제 관련 로직을 구현한 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    @Override
    public Question getQuestion(Long qId) {
        return questionRepository.findWithChoicesById(qId)
                .orElseThrow(() -> new ApiException(ErrorCode.QUESTION_NOT_FOUND));
    }

    @Transactional
    @Override
    public Long saveQuestion(Question question) {
        return questionRepository.save(question).getId();
    }
}
