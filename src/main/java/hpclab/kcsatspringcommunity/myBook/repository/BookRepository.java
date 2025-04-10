package hpclab.kcsatspringcommunity.myBook.repository;

import hpclab.kcsatspringcommunity.myBook.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 나만의 문제 정보를 DB와 상호작용하는 Spring Data JPA 인터페이스입니다.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * 회원 email을 통해 Book을 가져오는 메서드입니다.
     *
     * @param email 회원 email 아이디
     * @return 나만의 문제 Book 객체를 반환합니다.
     */
    Optional<Book> findByEmail(String email);
}
