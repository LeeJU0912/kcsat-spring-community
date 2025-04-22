package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.question.domain.Question;

/**
 * 문제 관련 로직을 정의한 인터페이스입니다.
 */
public interface QuestionService {

    /**
     * 문제 ID를 이용하여 문제 객체를 가져오는 메서드입니다.
     *
     * @param qId 문제 ID
     * @return 문제 객체를 반환합니다.
     */
    Question getQuestion(Long qId);

    /**
     * 문제 객체를 DB에 저장하는 메서드입니다.
     *
     * @param question 생성된 문제 엔티티 데이터
     * @return 저장된 question ID를 반환합니다.
     */
    Long saveQuestion(Question question);
}