package hpclab.kcsatspringcommunity.admin.domain;

import hpclab.kcsatspringcommunity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>이 클래스는 요청 사항 엔티티 클래스입니다.</p>
 * <p>회원 요청 사항을 저장하는 클래스입니다.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends BaseTimeEntity {

    /**
     * DB에서 자동으로 생성하는 기본키 값입니다.
     */
    @Id
    @GeneratedValue
    @Column(name = "user_request_id")
    private Long id;

    /**
     * 회원 요청 사항에 따라 타입이 구별됩니다.
     * 자세한 사항은 RequestType 참조.
     */
    @Column(name = "user_request_type", nullable = false)
    private RequestType type;

    /**
     * 회원 요청 사항 본문입니다.
     * 최대 2048자만 적을 수 있습니다.
     */
    @Column(name = "user_request_content", nullable = false, length = 2048)
    private String content;

    /**
     * 요청 사항을 등록한 회원 이름입니다.
     */
    @Column(name = "user_request_member_name", nullable = false)
    private String username;

    /**
     * 오류가 있는 문제를 신고하는 경우, 문제 ID도 같이 등록됩니다. (이외에는 null)
     */
    @Column(name = "user_request_question_id")
    private Long qId;
}