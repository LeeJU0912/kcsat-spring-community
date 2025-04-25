package hpclab.kcsatspringcommunity.myBook.domain;

import hpclab.kcsatspringcommunity.BaseTimeEntity;
import hpclab.kcsatspringcommunity.question.domain.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Book:Question 다대다 매핑을 위해 중단 다리로 객체를 할당하여 다대일:일대다 관계로 분할
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookQuestion extends BaseTimeEntity {

    /**
     * DB 자동 생성되는 ID 값
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 나만의 문제집
     */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * 생성된 문제
     */
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public BookQuestion(Book book, Question question) {
        this.book = book;
        this.question = question;
    }
}