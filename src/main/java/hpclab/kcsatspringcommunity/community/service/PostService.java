package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.dto.PostDetailForm;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostWriteForm;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 회원 커뮤니티 게시판의 게시글 관련 상호작용 로직을 정의한 인터페이스입니다.
 */
public interface PostService {

    /**
     * 게시글을 최초 등록하는 메서드입니다.
     *
     * @param postWriteForm 게시글 내용이 적혀있는 DTO 객체
     * @param email 게시글 등록 회원 email 아이디
     * @return 게시글 ID를 반환합니다.
     */
    Long savePost(PostWriteForm postWriteForm, String email);

    /**
     * (false인 경우 보완 필요)
     *
     * 게시글에 첨부된 문제를 MyBook에 저장하는 메서드입니다.
     *
     * @param qId 문제 ID
     * @param userEmail 회원 email 아이디
     * @return 문제 저장에 성공했다면 true를 반환합니다.
     */
    boolean saveQuestionFromPost(Long qId, String userEmail);

    /**
     * 게시글 목록을 조회하는 메서드입니다.
     *
     * @param pageable 페이지 정보 객체
     * @return Page 단위로 게시글을 조회하여 반환합니다.
     */
    Page<PostResponseForm> getPostList(Pageable pageable);

    /**
     * 게시글 첨부 문제 유형, 검색어로 필터링한 후, 게시글 목록을 조회하는 메서드입니다.
     *
     * @param pageable 페이지 정보 객체
     * @param keyword 게시글 검색 키워드
     * @param type 첨부 문제 유형
     * @return Page 단위로 게시글을 조회하여 반환합니다.
     */
    Page<PostResponseForm> getFindPostList(Pageable pageable, String keyword, QuestionType type);

    /**
     * 인기 게시글 목록을 조회하는 메서드입니다.
     *
     * @param pageable 페이지 정보 객체
     * @return Page 단위로 인기 게시글을 조회하여 반환합니다.
     */
    Page<PostResponseForm> getHotPostList(Pageable pageable);

    /**
     * 인기 게시글 첨부 문제 유형, 검색어로 필터링한 후, 인기 게시글 목록을 조회하는 메서드입니다.
     *
     * @param pageable 페이지 정보 객체
     * @param keyword 인기 게시글 검색 키워드
     * @param type 첨부 문제 유형
     * @return Page 단위로 인기 게시글을 조회하여 반환합니다.
     */
    Page<PostResponseForm> getFindHotPostList(Pageable pageable, String keyword, QuestionType type);

    /**
     * pId에 맞는 게시글을 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 상세 정보를 반환합니다.
     */
    PostDetailForm getPost(Long pId);

    /**
     * pId에 맞는 게시글을 제출한 양식에 맞게 수정하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @param postWriteForm 게시글 수정 양식
     * @return 게시글 상세 정보를 반환합니다.
     */
    PostDetailForm updatePost(Long pId, PostWriteForm postWriteForm);

    /**
     * pId에 맞는 게시글을 삭제하는 메서드입니다.
     *
     * @param pId 게시글 ID
     */
    void removePost(Long pId);

    /**
     * 게시글을 최초 등록시 조회수, 추천, 비추천수를 0으로 초기화하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 조회수를 반환합니다.(0)
     */
    String setPostCount(Long pId);

    /**
     * 게시글 조회수를 1 증가시키는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @param userEmail 회원 email 아이디
     * @return 게시글 조회수를 반환합니다.
     */
    String increasePostViewCount(Long pId, String userEmail);

    /**
     * 게시글 조회수를 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 조회수를 반환합니다.
     */
    String getPostViewCount(Long pId);

    /**
     * 게시글 추천수를 1 증가시키는 메서드입니다.
     * <p><b>회원은 1일 2회 이상 추천/비추천할 수 없습니다.</b></p>
     *
     * @param pId 게시글 ID
     * @param userEmail 회원 email 아이디
     * @return 게시글 추천수를 반환합니다.
     */
    String increasePostVoteCount(Long pId, String userEmail);

    /**
     * 게시글 비추천수를 1 증가시키는 메서드입니다.
     * <p><b>회원은 1일 2회 이상 추천/비추천할 수 없습니다.</b></p>
     *
     * @param pId 게시글 ID
     * @param userEmail 회원 email 아이디
     * @return 게시글 비추천수를 반환합니다.
     */
    String decreasePostVoteCount(Long pId, String userEmail);

    /**
     * 게시글 추천수를 가져오는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 추천수를 반환합니다.
     */
    String getIncreasePostVoteCount(Long pId);

    /**
     * 게시글 비추천수를 가져오는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 비추천수를 반환합니다.
     */
    String getDecreasePostVoteCount(Long pId);

    /**
     * 게시글 조회 기록을 초기화합니다.
     * 매일 자정에 자동 실행되는 메서드입니다.
     * 이 함수를 통해 1일 2회 이상 추천/비추천을 불가능하게 조절.
     */
    void resetPostView();
}
