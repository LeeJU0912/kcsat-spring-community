package hpclab.kcsatspringcommunity.community.repository;

import hpclab.kcsatspringcommunity.community.domain.Comment;
import hpclab.kcsatspringcommunity.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 댓글 정보를 DB와 상호작용하는 Spring Data JPA 인터페이스입니다.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글 정보로 댓글을 찾는 메서드입니다.
     *
     * @param post 게시글 객체입니다.
     * @return post 파라미터 게시글 안에 있는 모든 댓글 리스트를 반환합니다.
     */
    List<Comment> findByPost(Post post);

    /**
     * 댓글 ID와 일치하는 댓글을 조회하는 메서드입니다.
     * 댓글을 조회할 때, 회원 정보도 같이 조회하도록 합니다. (N+1 문제 방지)
     * 댓글 삭제하는 용도로 사용.
     *
     * @param cId 댓글 ID
     * @return 댓글 ID에 맞는 댓글을 반환합니다. 만약 찾는 댓글이 없는 경우, NoSuchElementException을 반환합니다.
     */
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.member WHERE c.cId = :cId")
    Optional<Comment> findCommentWithMember(Long cId);
}