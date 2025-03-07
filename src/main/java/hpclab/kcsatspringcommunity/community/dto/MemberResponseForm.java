package hpclab.kcsatspringcommunity.community.dto;

import hpclab.kcsatspringcommunity.community.domain.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberResponseForm {

    private LocalDateTime createdDate;

    private Long mId;
    private String email;
    private String username;

    @Builder
    public MemberResponseForm(Member member) {
        this.mId = member.getMID();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.createdDate = member.getCreatedDate();
    }
}
