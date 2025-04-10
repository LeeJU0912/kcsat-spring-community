package hpclab.kcsatspringcommunity.community.dto;

import hpclab.kcsatspringcommunity.community.domain.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 회원 간소화 정보 DTO 변환 클래스입니다.
 */
@Data
public class MemberResponseForm {

    /**
     * 회원가입 일자
     */
    private LocalDateTime createdDate;

    /**
     * 회원 ID
     */
    private Long mId;

    /**
     * 회원 email 아이디
     */
    private String email;

    /**
     * 회원 별명
     */
    private String username;

    @Builder
    public MemberResponseForm(Member member) {
        this.mId = member.getMID();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.createdDate = member.getCreatedDate();
    }
}
