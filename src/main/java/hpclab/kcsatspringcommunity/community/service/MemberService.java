package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.dto.MemberSignUpForm;
import hpclab.kcsatspringcommunity.community.dto.MemberResponseForm;

import java.util.List;

/**
 * 회원 정보 관련 상호작용 로직을 정의한 인터페이스입니다.
 */
public interface MemberService {

    /**
     * 회원 가입 메서드입니다.
     *
     * @param memberSignUpForm 회원 가입 양식 Form DTO 객체
     */
    void signUp(MemberSignUpForm memberSignUpForm);

    /**
     * 전체 회원을 조회하는 메서드입니다.
     *
     * @return 회원 조회 DTO 객체를 목록으로 담아 반환합니다.
     */
    List<MemberResponseForm> findMembers();

    /**
     * 회원 email 아이디를 통해 회원 별명을 반환하는 메서드입니다.
     *
     * @param userEmail 회원 email 아이디
     * @return 회원의 별명을 반환합니다.
     */
    String findUsername(String userEmail);
}