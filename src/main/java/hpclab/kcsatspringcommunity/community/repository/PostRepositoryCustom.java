package hpclab.kcsatspringcommunity.community.repository;

import hpclab.kcsatspringcommunity.community.domain.Post;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 게시글 조회 중 특수한 경우에 대해 불러오는 커스텀 인터페이스입니다.
 */
public interface PostRepositoryCustom {

    /**
     * 게시글에 담긴 문제 유형으로 DB에서 찾고, 제목으로 2차로 필터링하는 메서드입니다.
     * 게시판에서 특정 유형만이 담긴 게시글을 보고싶을 때 사용합니다.
     *
     * @param pageable 페이지 정보를 담은 객체
     * @param title 게시글 제목
     * @param type 게시글에 담긴 문제 유형
     * @return Page 단위로 게시글 목록이 반환됩니다.
     */
    Page<Post> findPostsByQuestionTypeAndTitle(Pageable pageable, String title, QuestionType type);

    /**
     * 인기 게시글만 DB에서 조회하여 반환하는 메서드입니다.
     *
     * @param pageable 페이지 정보를 담은 객체
     * @return Page 단위로 게시글 목록이 반환됩니다.
     */
    Page<Post> findHotPosts(Pageable pageable);

    /**
     * 인기 게시글 중, 게시글에 담긴 문제 유형으로 DB에서 찾고, 제목으로 2차로 필터링하는 메서드입니다.
     * 인기 게시판에서 특정 유형만이 담긴 게시글을 보고싶을 때 사용합니다.
     *
     * @param pageable 페이지 정보를 담은 객체
     * @param title 게시글 제목
     * @param type 게시글에 담긴 문제 유형
     * @return Page 단위로 게시글 목록이 반환됩니다.
     */
    Page<Post> findHotPostsByQuestionTypeAndTitle(Pageable pageable, String title, QuestionType type);
}
