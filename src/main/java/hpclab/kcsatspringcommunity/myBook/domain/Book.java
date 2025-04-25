package hpclab.kcsatspringcommunity.myBook.domain;

import hpclab.kcsatspringcommunity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 나만의 문제를 담은 객체 엔티티 클래스입니다.
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseTimeEntity {

    /**
     * MyBook ID. DB에서 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 회원 email 아이디
     */
    @Column(name = "member_email", nullable = false, unique = true)
    private String email;

    /**
     * 다대다 매핑을 위해 중단 다리로 BookQueston 객체를 할당하여 다대일:일대다 관계로 분할
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookQuestion> bookQuestions;

    public Book(String email) {
        this.email = email;
    }
}