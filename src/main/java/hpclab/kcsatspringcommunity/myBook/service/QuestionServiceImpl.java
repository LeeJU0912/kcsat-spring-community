package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionJPARepository questionJPARepository;

    @Override
    public Question getQuestion(Long qId) {
        return questionJPARepository.findById(qId).orElseThrow(() -> new IllegalArgumentException("Question not found"));
    }
}
