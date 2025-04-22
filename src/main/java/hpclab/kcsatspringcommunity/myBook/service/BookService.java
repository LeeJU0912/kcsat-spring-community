package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.myBook.domain.Book;

/**
 * 회원 관련 나만의 문제집을 생성/조회 하는 기능을 정의한 인터페이스입니다.
 */
public interface BookService {

    /**
     * 최초 회원가입시, 나만의 문제집을 새롭게 생성하는 메서드입니다.
     *
     * @param userEmail 회원 email 아이디
     */
    void makeBook(String userEmail);

    /**
     * 회원 email 아이디로 회원의 나만의 문제집을 반환하는 메서드입니다.
     *
     * @param userEmail 회원 email 아이디
     * @return 나만의 문제집 DTO 반환
     */
    Book findBook(String userEmail);
}