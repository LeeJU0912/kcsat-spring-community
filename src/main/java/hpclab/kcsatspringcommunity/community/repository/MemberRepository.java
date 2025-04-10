package hpclab.kcsatspringcommunity.community.repository;

import hpclab.kcsatspringcommunity.community.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 정보를 DB와 상호작용하는 Spring Data JPA 인터페이스입니다.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 회원 email 아이디로 회원 정보를 DB에서 불러오는 메서드입니다.
     *
     * @param email 회원 email 아이디
     * @return 만약 회원을 찾는다면 Member 회원 객체를, 찾지 못한다면 UsernameNotFoundException을 반환합니다.
     */
    Optional<Member> findByEmail(String email);

    /**
     * 회원 email 아이디와 일치하는 아이디가 있는지 여부를 체크하는 메서드입니다.
     *
     * @param email 회원 email 아이디
     * @return 만약 회원이 이미 DB에 있다면 true, 그렇지 않다면 false를 반환합니다.
     */
    boolean existsByEmail(String email);
}