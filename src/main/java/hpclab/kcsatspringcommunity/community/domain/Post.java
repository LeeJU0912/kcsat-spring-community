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
 * 필요한 요소
 * 0. pid
 * 1. 게시글 작성자
 * 2. 글 제목
 * 3. 본문
 * 4.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long pId;

    @Column(nullable = false)
    private String postTitle;
    @Column(nullable = false)
    private String postContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment;

    private QuestionType questionType;

    private Boolean isHotPost;

    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    public void update(String postTitle, String postContent) {
        this.postTitle = postTitle;
        this.postContent = postContent;
    }

    public void gettingHot() {
        this.isHotPost = true;
    }
}