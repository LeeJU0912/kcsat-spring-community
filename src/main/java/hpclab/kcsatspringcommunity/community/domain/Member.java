package hpclab.kcsatspringcommunity.community.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hpclab.kcsatspringcommunity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 회원 정보를 담은 엔티티 객체입니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    /**
     * 회원 ID 입니다. DB에서 자동 생성되는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long mID;

    /**
     * 회원의 권한을 나타냅니다. 자세한 사항은 Role 참조.
     */
    private Role role;

    /**
     * 회원 로그인시 사용될 email 아이디를 저장합니다.
     * 중복된 값을 허용하지 않습니다.
     */
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일을 입력해주세요.")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 회원 별명을 저장합니다.
     * 2~12자로 지정이 가능합니다.
     */
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 12, message = "닉네임은 2 ~ 12자 사이로 입력해주세요.")
    private String username;

    /**
     * 회원 비밀번호를 저장합니다.
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Column(name = "MEMBER_PW", nullable = false)
    private String password;

    /**
     * 회원이 작성한 게시글들을 저장합니다.
     */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"member"})
    private List<Post> posts;

    /**
     * 회원이 작성한 댓글들을 저장합니다.
     */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"member"})
    private List<Comment> comments;

//    @OneToOne
//    @JoinColumn(name = "BOOK_ID")
//    private Book book;
}
