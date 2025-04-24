package hpclab.kcsatspringcommunity.community.controller;

import hpclab.kcsatspringcommunity.util.JWTUtil;
import hpclab.kcsatspringcommunity.community.dto.PostResponseForm;
import hpclab.kcsatspringcommunity.community.dto.PostWriteForm;
import hpclab.kcsatspringcommunity.community.service.PostService;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ApiResponse;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hpclab.kcsatspringcommunity.util.JWTUtil.USER_EMAIL;
import static hpclab.kcsatspringcommunity.exception.SuccessCode.POST_DELETE_SUCCESS;

/**
 * <p>회원 커뮤니티 게시판 컨트롤러 클래스입니다.</p>
 *
 * <p>기능 목록</p>
 * <ul>
 *     <li>게시글 목록 조회</li>
 *     <li>게시글 상세 정보 조회</li>
 *     <li>게시글 작성/수정/삭제</li>
 *     <li>게시글 작성 시 자신이 만든 문제 첨부 가능</li>
 *     <li>댓글 작성/삭제</li>
 *     <li>게시글 첨부 문제 마이북에 저장</li>
 *     <li>게시글, 댓글 추천/비추천</li>
 *     <li>댓글, 인기 댓글 조회</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final QuestionService questionService;
    private final BookService bookService;

    private final JWTUtil jwtUtil;


    /**
     * 회원 커뮤니티 게시판 게시글 목록을 Page 갯수 단위로 조회하는 메서드입니다.
     * 기본 크기는 10개 단위로 조회합니다.
     * 검색어 파라미터를 넣는 경우, 검색어 조건에 맞는 게시글만 검색하여 조회합니다.
     *
     * @param page 페이지 번호 (기본값 0)
     * @param size 페이지 크기 (기본값 10)
     * @param sort 정렬 기준 (기본값 pId 내림차순)
     * @param keyword 검색어 (optional)
     * @param type 타입 필터 (optional)
     * @return 게시글 목록을 Page 단위로 묶어서 반환합니다.
     */
    @GetMapping("/open/board")
    public ResponseEntity<ApiResponse<Page<PostResponseForm>>> getPostListByPage(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size,
                                                                                 @RequestParam(defaultValue = "pId,DESC") String sort,
                                                                                 @RequestParam(required = false) String keyword,
                                                                                 @RequestParam(required = false) QuestionType type
                                                                    ) {

        // 정렬 기준 처리
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);  // ASC or DESC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        if ((keyword == null || keyword.isEmpty()) && type == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, postService.getPostList(pageable), null, null));
        }
        else {
            return ResponseEntity.ok(new ApiResponse<>(true, postService.getFindPostList(pageable, keyword, type), null, null));
        }
    }

    /**
     * 회원 커뮤니티 인기글 게시판 게시글 목록을 Page 갯수 단위로 조회하는 메서드입니다.
     * 기본 크기는 10개 단위로 조회합니다.
     * 검색어 파라미터를 넣는 경우, 검색어 조건에 맞는 게시글만 검색하여 조회합니다.
     *
     * @param page 페이지 번호 (기본값 0)
     * @param size 페이지 크기 (기본값 10)
     * @param sort 정렬 기준 (기본값 pId 내림차순)
     * @param keyword 검색어 (optional)
     * @param type 타입 필터 (optional)
     * @return 게시글 목록을 Page 단위로 묶어서 반환합니다.
     */
    @GetMapping("/open/board/hot")
    public ResponseEntity<ApiResponse<Page<PostResponseForm>>> hotBoard(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "pId,DESC") String sort,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) QuestionType type
                                                           ) {

        // 정렬 기준 처리
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);  // ASC or DESC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        if ((keyword == null || keyword.isEmpty()) && type == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, postService.getHotPostList(pageable), null, null));
        }
        else {
            return ResponseEntity.ok(new ApiResponse<>(true, postService.getFindHotPostList(pageable, keyword, type), null, null));
        }
    }

    /**
     * 회원 커뮤니티 게시판 게시글 정보를 상세 조회하는 메서드입니다.
     *
     * @param token 유저 JWT 토큰값
     * @param pId 게시글 ID
     * @return 게시글 상세 정보를 반환합니다.
     */
    @GetMapping("/board/post/{pId}")
    public ResponseEntity<ApiResponse<PostResponseForm>> board(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        postService.increasePostViewCount(pId, userEmail);

        PostResponseForm post = new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId)));

        return ResponseEntity.ok(new ApiResponse<>(true, post, null, null));
    }

    /**
     * 회원 커뮤니티 게시글 추천 수를 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 추천 수를 반환합니다.
     */
    @GetMapping("/board/post/{pId}/vote/up")
    public ResponseEntity<ApiResponse<String>> getUpVotePost(@PathVariable Long pId) {
        return ResponseEntity.ok(new ApiResponse<>(true, postService.getIncreasePostVoteCount(pId), null, null));
    }


    /**
     * 회원 커뮤니티 게시글 비추천 수를 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 비추천 수를 반환합니다.
     */
    @GetMapping("/board/post/{pId}/vote/down")
    public ResponseEntity<ApiResponse<String>> getDownVotePost(@PathVariable Long pId) {
        return ResponseEntity.ok(new ApiResponse<>(true, postService.getDecreasePostVoteCount(pId), null, null));
    }


    /**
     * 회원 커뮤니티 게시글을 추천하는 메서드입니다.
     * 하루에 2번 이상 다시 추천/비추천할 수 없습니다. 자세한 사항은 postService 참조.
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 게시글 추천 수를 반환합니다.
     */
    @PostMapping("/board/post/{pId}/vote/up")
    public ResponseEntity<ApiResponse<String>> upVotePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();
        return ResponseEntity.ok(new ApiResponse<>(true, postService.increasePostVoteCount(pId, userEmail), null, null));
    }


    /**
     * 회원 커뮤니티 게시글을 비추천하는 메서드입니다.
     * 하루에 2번 이상 다시 추천/비추천할 수 없습니다. 자세한 사항은 postService 참조.
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 게시글 비추천 수를 반환합니다.
     */
    @PostMapping("/board/post/{pId}/vote/down")
    public ResponseEntity<ApiResponse<String>> downVotePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();
        return ResponseEntity.ok(new ApiResponse<>(true, postService.decreasePostVoteCount(pId, userEmail), null, null));
    }


    /**
     * 회원 커뮤니티 게시글을 새롭게 등록하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 등록 게시글 정보가 담긴 객체
     * @return 게시글을 저장하고 해당 게시글 상세 정보를 반환합니다.
     */
    @PostMapping("/board/post")
    public ResponseEntity<ApiResponse<PostResponseForm>> writePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PostWriteForm form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        Long pId = postService.savePost(form, userEmail);

        PostResponseForm postResponseForm = new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId)));
        return ResponseEntity.ok(new ApiResponse<>(true, postResponseForm, null, null));
    }


    /**
     * 회원 커뮤니티 게시판 게시글을 수정하는 메서드입니다.
     * <p><b>작성자만 수정이 가능합니다.</b></p>
     *
     * @param pId 게시글 ID
     * @param form 수정 사항이 담긴 DTO 객체
     * @return 수정된 게시글 상세 정보를 담아 반환합니다.
     */
    @PutMapping("/board/post/{pId}")
    public ResponseEntity<ApiResponse<PostResponseForm>> updateBoard(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                      @PathVariable Long pId,
                                                      @RequestBody PostWriteForm form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        if (!userEmail.equals(postService.getPost(pId).getMember().getEmail())) {
            throw new ApiException(ErrorCode.USER_VERIFICATION_FAILED);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, postService.updatePost(pId, form), null, null));
    }


    /**
     * 회원 커뮤니티 게시판 게시글을 삭제하는 메서드입니다.
     * <p><b>작성자만 삭제가 가능합니다.</b></p>
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 권한이 확인되었고 정상적으로 삭제된다면 ok, 이외의 경우에는 BAD_REQUEST 발생.
     */
    @DeleteMapping("/board/post/{pId}")
    public ResponseEntity<ApiResponse<String>> removeBoard(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        PostResponseForm post = new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId)));

        if (!userEmail.equals(post.getEmail())) {
            throw new ApiException(ErrorCode.USER_VERIFICATION_FAILED);
        }

        postService.removePost(pId);

        return ResponseEntity.ok(new ApiResponse<>(true, null, POST_DELETE_SUCCESS.getCode(), POST_DELETE_SUCCESS.getMessage()));
    }


    /**
     * 게시글에 첨부된 문제를 저장하는 메서드입니다.
     * 사용자 별로 개별적으로 마이북에 문제가 저장됩니다.
     *
     * @param qId 게시글 ID
     * @return 게시글에 첨부된 문제가 정상적으로 저장되면 ok, 그렇지 않으면 BAD_REQUEST 반환.
     */
    @PostMapping("/board/post/{qId}/question")
    public ResponseEntity<ApiResponse<String>> saveQuestionFromPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Long qId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        return ResponseEntity.ok(new ApiResponse<>(true, postService.saveQuestionFromPost(qId, userEmail).toString(), null, null));
    }


    /**
     * 게시글에 문제를 첨부하기 위해, 나의 문제 목록을 보여주는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @return 회원이 저장한 모든 문제들의 리스트를 보여줍니다.
     */
    @GetMapping("/board/post/uploadQuestion")
    public ResponseEntity<ApiResponse<BookResponseForm>> getUserQuestions(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        return ResponseEntity.ok(new ApiResponse<>(true, new BookResponseForm(bookService.findBook(userEmail)), null, null));
    }


    /**
     * 첨부할 문제 ID를 요청하면, 첨부되는 문제 상세 정보를 반환합니다.
     *
     * @param qId 첨부할 문제 ID
     * @return 문제 상세 정보를 반환합니다.
     */
    @PostMapping("/board/post/uploadQuestion")
    public ResponseEntity<ApiResponse<QuestionResponseForm>> uploadUserQuestion(@RequestParam Long qId) {

        Question question = questionService.getQuestion(qId);

        return ResponseEntity.ok(new ApiResponse<>(true, new QuestionResponseForm(question), null, null));
    }
}