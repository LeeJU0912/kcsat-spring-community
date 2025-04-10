package hpclab.kcsatspringcommunity.community.dto;

import hpclab.kcsatspringcommunity.community.domain.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 상세 정보 DTO 변환 클래스입니다.
 */
@Data
public class MemberDetailsResponseForm {

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

    /**
     * 회원가입 일자
     */
    private LocalDateTime createdDate;

    /**
     * 회원 작성 글 목록
     */
    private List<PostResponseForm> posts;

    /**
     * 회원 작성 댓글 목록
     */
    private List<CommentResponseForm> comments;

    @Builder
    public MemberDetailsResponseForm(Member member) {
        this.mId = member.getMID();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.createdDate = member.getCreatedDate();
        this.posts = member.getPosts().stream().map(PostResponseForm::new).toList();
        this.comments = member.getComments().stream().map(CommentResponseForm::new).toList();
    }
}
