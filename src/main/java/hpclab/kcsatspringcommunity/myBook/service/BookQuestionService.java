package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.question.domain.Question;

/**
 * 나만의 문제집에 문제를 저장하는 로직을 정의한 인터페이스입니다.
 */
public interface BookQuestionService {

    /**
     * 문제 생성 후, 최초로 저장할 때 사용하는 메서드입니다.
     *
     * @param question 문제 양식
     * @param userEmail 문제를 저장하는 회원 email 아이디
     * @return 회원의 Book ID를 반환합니다.
     */
    Long saveFirstQuestion(Question question, String userEmail);

    /**
     * 다른 회원이 게시글에 첨부한 문제를 저장할 때 사용하는 메서드입니다.
     *
     * @param qId 문제 ID
     * @param userEmail 문제를 저장하는 회원 email 아이디
     * @return 회원의 Book ID를 반환합니다.
     */
    Long saveQuestion(Long qId, String userEmail);
}
