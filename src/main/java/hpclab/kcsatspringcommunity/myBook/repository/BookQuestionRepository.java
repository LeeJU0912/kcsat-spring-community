package hpclab.kcsatspringcommunity.myBook.repository;

import hpclab.kcsatspringcommunity.myBook.domain.BookQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * BookQuestion 정보를 DB와 상호작용하는 Spring Data JPA 인터페이스입니다.
 */
public interface BookQuestionRepository extends JpaRepository<BookQuestion, Long> {
}
