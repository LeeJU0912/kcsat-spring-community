package hpclab.kcsatspringcommunity.community.dto;


import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시글 저장 정보 DTO 클래스입니다.
 * 댓글, 추천수 등 모든 정보가 담긴 클래스는 PostDetailForm 참조.
 */
@Data
@AllArgsConstructor
public class PostResponseForm {

    /**
     * 게시글 ID
     */
    private Long pId;

    /**
     * 게시글 제목
     */
    private String title;

    /**
     * 게시글 본문
     */
    private String content;

    /**
     * 게시글 작성 시간
     */
    private LocalDateTime postDate;

    /**
     * 게시글 email 아이디
     */
    private String email;

    /**
     * 게시글 별명
     */
    private String username;

    /**
     * 게시글 첨부 문제 유형
     */
    private String questionType;

    /**
     * 게시글 첨부 문제 세부 사항 DTO
     */
    private QuestionDetailsDto question;

    /**
     * 게시글 조회수
     */
    private Long postViewCount;

    @Builder
    public PostResponseForm(Post post, Long postViewCount) {
        this.pId = post.getId();
        this.title = post.getPostTitle();
        this.content = post.getPostContent();
        this.postDate = post.getCreatedDate();
        this.email = post.getMember().getEmail();
        this.username = post.getMember().getUsername();
        this.postViewCount = postViewCount;

        Question question = post.getQuestion();

        if (question == null) {
            this.questionType = "";
        }
        else {
            this.questionType = question.getType().getKrName();
            this.question = new QuestionDetailsDto(question);
        }
    }

    public PostResponseForm(Post post) {
        this.pId = post.getId();
        this.title = post.getPostTitle();
        this.content = post.getPostContent();
        this.postDate = post.getCreatedDate();
        this.email = post.getMember().getEmail();
        this.username = post.getMember().getUsername();

        Question question = post.getQuestion();

        if (question == null) {
            this.questionType = "";
        }
        else {
            this.questionType = question.getType().getKrName();
            this.question = new QuestionDetailsDto(question);
        }
    }
}
