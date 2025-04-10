package hpclab.kcsatspringcommunity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 기본 시간 엔티티 클래스입니다.
 * 이 클래스를 extends 하면 @MappedSuperclass에 의해 이 함수의 필드들도 해당 객체에 같이 연동됩니다.
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {

    /**
     * 최초 생성 시각을 자동으로 기록합니다.
     * 이후 수정이 불가능합니다.
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    /**
     * 가장 최근 수정 시각을 자동으로 기록합니다.
     * 최초 생성은 불가능합니다. (객체 생성 이후 수정은 가능)
     */
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}