package hpclab.kcsatspringcommunity.community.domain;

import hpclab.kcsatspringcommunity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 커뮤니티 게시판 게시글의 댓글 엔티티 객체입니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    /**
     * 댓글 ID 입니다. DB가 자동으로 값을 생성합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long cId;

    /**
     * 댓글 내용입니다.
     */
    @Column(nullable = false)
    private String content;

    /**
     * 댓글이 달린 게시글 객체입니다. 댓글:게시글 다대일 관계입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    /**
     * 댓글을 작성한 회원 객체입니다. 댓글:회원 다대일 관계입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}