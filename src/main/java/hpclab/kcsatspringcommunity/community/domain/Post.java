package hpclab.kcsatspringcommunity.community.domain;

import hpclab.kcsatspringcommunity.BaseTimeEntity;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 회원 커뮤니티 게시판의 게시글 정보를 담은 엔티티 객체입니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Post extends BaseTimeEntity {

    /**
     * 게시글 ID 입니다. DB에서 자동 생성되는 값입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long pId;

    /**
     * 게시글 제목입니다.
     */
    @Column(nullable = false)
    private String postTitle;

    /**
     * 게시글 본문입니다.
     */
    @Column(nullable = false)
    private String postContent;

    /**
     * 게시글 작성자 정보입니다. 게시글:작성자 다대일 매칭 관계입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    /**
     * 게시글에 달린 댓글 모음입니다. 게시글:댓글 일대다 매칭 관계입니다.
     * 게시글을 지우면, 관련 댓글도 모두 자동으로 삭제됩니다.
     */
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment;

    /**
     * 게시글에 첨부한 문제 유형을 나타냅니다. 자세한 사항은 QuestionType 참조.
     */
    private QuestionType questionType;

    /**
     * 인기 게시글 여부를 나타냅니다. 인기 게시글이라면 HOT 게시판에서도 확인이 가능합니다.
     */
    private Boolean isHotPost;

    /**
     * 게시글에 첨부된 문제를 나타냅니다. 게시글:문제 다대일 매칭 관계입니다.
     */
    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    /**
     * 게시글을 수정하는 경우, 엔티티를 갱신하는 메서드입니다.
     *
     * @param postTitle 게시글 제목
     * @param postContent 게시글 본문
     */
    public void update(String postTitle, String postContent) {
        this.postTitle = postTitle;
        this.postContent = postContent;
    }

    /**
     * 이 게시글을 인기 게시글로 지정하는 메서드입니다.
     */
    public void gettingHot() {
        this.isHotPost = true;
    }
}