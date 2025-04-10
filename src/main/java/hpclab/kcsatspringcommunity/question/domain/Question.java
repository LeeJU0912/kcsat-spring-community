package hpclab.kcsatspringcommunity.question.domain;

import hpclab.kcsatspringcommunity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 생성된 문제를 저장하는 엔티티 클래스입니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Question extends BaseTimeEntity {

    /**
     * 문제 ID. DB가 자동으로 생성해줍니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;

    /**
     * 문제 유형입니다. 자세한 사항은 QuestionType 참조
     */
    private QuestionType type;

    /**
     * 문제 제목
     */
    private String title;

    /**
     * 문제 공유 수(게시판에서 공유한 수)
     */
    private Long shareCounter;

    /**
     * 문제 본문
     */
    @Column(length = 2048)
    private String mainText;

    /**
     * 문제 보기
     */
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "QUESTION_ID")
    private List<Choice> choices;

    /**
     * 문제 정답
     */
    private String answer;

    /**
     * 문제 번역
     */
    @Column(length = 2048)
    private String translation;

    /**
     * 문제 해설
     */
    @Column(length = 2048)
    private String explanation;

    /**
     * 문제 공유수 1 증가 메서드.
     */
    public void upShareCounter() {
        shareCounter++;
    }
}