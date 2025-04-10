package hpclab.kcsatspringcommunity.admin.dto;

import hpclab.kcsatspringcommunity.admin.domain.RequestType;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.question.domain.Question;
import lombok.Builder;
import lombok.Data;

/**
 * 회원 요청 사항 UserRequest 정보를 불러와 사용하는 데에 쓰이는 DTO 클래스입니다.
 */
@Data
@Builder
public class UserRequestResponseForm {

    /**
     * 회원 요청 사항 유형입니다.
     * 자세한 사항은 RequestType 참조.
     */
    private RequestType type;

    /**
     * 건의 사항을 등록한 회원 세부 정보입니다.
     */
    private Member member;

    /**
     * 문제도 같이 첨부되었을 경우, 문제에 대한 세부 정보입니다.
     */
    private Question question;

    /**
     * 회원 요청 사항 본문 내용입니다.
     */
    private String content;
}
