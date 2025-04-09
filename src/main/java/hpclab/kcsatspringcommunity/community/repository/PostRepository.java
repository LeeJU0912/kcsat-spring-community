package hpclab.kcsatspringcommunity.community.repository;

import hpclab.kcsatspringcommunity.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    /**
     * 게시글 ID에 대해 게시글 상세 정보를 가져웁니다.
     *
     * N+1 처리 Fetch join 적용으로 post 가져올 때 comment도 같이 가져오도록 합니다.
     * @param postId 게시글 ID
     * @return 게시글 상세 정보
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comment WHERE p.pId = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") Long postId);
}
