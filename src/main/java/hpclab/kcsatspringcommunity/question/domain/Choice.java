package hpclab.kcsatspringcommunity.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 안의 보기를 저장한 엔티티 클래스입니다.
 * 하나의 보기 당 하나의 클래스가 할당됩니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Choice {

    /**
     * 보기 ID. DB에서 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * 보기 항목
     */
    @Column(name = "choice", nullable = false)
    private String choice;

    public Choice(String choice) {
        this.choice = choice;
    }
}