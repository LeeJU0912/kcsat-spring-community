package hpclab.kcsatspringcommunity.admin.repository;

import hpclab.kcsatspringcommunity.admin.domain.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRequest와 상호작용하는 Spring Data JPA 인터페이스입니다.
 */
public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
}
