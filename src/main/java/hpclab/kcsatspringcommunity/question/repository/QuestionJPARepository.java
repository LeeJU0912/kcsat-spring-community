package hpclab.kcsatspringcommunity.question.repository;

import hpclab.kcsatspringcommunity.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 문제에 대한 DB 상호작용을 하는 Spring Data JPA 인터페이스입니다.
 */
public interface QuestionJPARepository extends JpaRepository<Question, Long> {

    /**
     * 문제 공유수가 limit 이상인 문제들만 조회하는 메서드입니다.
     * 문제 공유수가 특정 수 이상인 인기 문제들을 선별하기 위해 사용합니다.
     *
     * @param limit 문제 공유수에 하한을 정의한 변수
     * @return 문제 객체를 반환합니다.
     */
    List<Question> findAllByShareCounterGreaterThan(Long limit);

    /**
     * 문제를 조회할 때, 문제에 속한 보기도 동시에 조회하도록 하는 메서드입니다. (N+1 방지)
     *
     * @param qId 문제 ID
     * @return 조회한 문제 객체를 반환합니다.
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.choices WHERE q.id = :qId")
    Optional<Question> findWithChoicesById(@Param("qId") Long qId);
}
