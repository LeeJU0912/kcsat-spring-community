package hpclab.kcsatspringcommunity.community.dto;

import hpclab.kcsatspringcommunity.community.domain.Member;
import lombok.Data;

@Data
public class MemberAuthResponseForm {
    private String email;
    private String username;
    private String role;

    public MemberAuthResponseForm(Member member) {
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.role = member.getRole().getValue();
    }
}
