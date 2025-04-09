package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.question.domain.Question;

public interface QuestionService {
    Question getQuestion(Long qId);
}