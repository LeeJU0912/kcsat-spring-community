package hpclab.kcsatspringcommunity.admin.service;

import hpclab.kcsatspringcommunity.admin.dto.UserRequestRequestForm;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestResponseForm;

import java.util.List;

/**
 * 회원 요청 사항 처리 로직인 UserRequestService의 기능 목록을 정의한 인터페이스입니다.
 */
public interface UserRequestService {

    /**
     * 회원이 문제 오류 요청을 하는 경우, 처리하는 메서드입니다.
     *
     * @param qId 문제 ID
     * @param email 회원 email 아이디
     * @return 문제 ID와 회원 정보를 담아 UserRequestResponseForm DTO로 반환합니다.
     */
    UserRequestResponseForm getQuestionErrorForm(Long qId, String email);

    /**
     * 회원이 건의 사항 요청을 하는 경우, 처리하는 메서드입니다.
     *
     * @param form 회원 건의 사항 본문 메시지
     * @param email 회원 email 아이디
     * @return 건의 사항 메시지와 회원 정보를 담아 UserRequestResponseForm DTO로 반환합니다.
     */
    UserRequestResponseForm getImprovingForm(UserRequestRequestForm form, String email);

    /**
     * 회원이 기타 요청을 하는 경우, 처리하는 메서드입니다.
     *
     * @param form 회원 건의 사항 본문 메시지
     * @param email 회원 email 아이디
     * @return 건의 사항 메시지와 회원 정보를 담아 UserRequestResponseForm DTO로 반환합니다.
     */
    UserRequestResponseForm getETCForm(UserRequestRequestForm form, String email);

    /**
     * 회원이 요청한 사항이 담긴 UserRequestResponseForm 객체를 보완합니다.
     * 만약 문제 ID가 포함이 되어 있다면, UserRequestResponseForm qID 항목에 추가합니다.
     * 회원 email 아이디도 UserRequestResponseForm username 객체에 추가로 등록합니다.
     *
     * @param form 회원 요청 사항 정보
     * @param userEmail 회원 email 아이디
     * @return 추가 정보를 담아 UserRequestResponseForm을 반환합니다.
     */
    UserRequestResponseForm updateUserRequestForm(UserRequestResponseForm form, String userEmail);

    /**
     * 모든 회원 요청 사항에 대해 DB에서 불러오는 메서드입니다.
     *
     * @return 회원 요청 사항 모음
     */
    List<UserRequestResponseForm> getUserRequests();
}
