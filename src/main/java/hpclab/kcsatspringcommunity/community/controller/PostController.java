package hpclab.kcsatspringcommunity.community.controller;

import hpclab.kcsatspringcommunity.JWTUtil;
import hpclab.kcsatspringcommunity.community.dto.*;
import hpclab.kcsatspringcommunity.community.service.CommentService;
import hpclab.kcsatspringcommunity.community.service.PostService;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static hpclab.kcsatspringcommunity.JWTUtil.AUTHORIZATION;
import static hpclab.kcsatspringcommunity.JWTUtil.USER_EMAIL;

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
    @GetMapping("/api/community/open/board")
    public ResponseEntity<Page<PostResponseForm>> getPostListByPage(@RequestParam(defaultValue = "0") int page,
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
            return ResponseEntity.ok(postService.getPostList(pageable));
        }
        else {
            return ResponseEntity.ok(postService.getFindPostList(pageable, keyword, type));
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
    @GetMapping("/api/community/open/board/hot")
    public ResponseEntity<Page<PostResponseForm>> hotBoard(@RequestParam(defaultValue = "0") int page,
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
            return ResponseEntity.ok(postService.getHotPostList(pageable));
        }
        else {
            return ResponseEntity.ok(postService.getFindHotPostList(pageable, keyword, type));
        }
    }

    /**
     * 회원 커뮤니티 게시판 게시글 정보를 상세 조회하는 메서드입니다.
     *
     * @param token 유저 JWT 토큰값
     * @param pId 게시글 ID
     * @return 게시글 상세 정보를 반환합니다.
     */
    @GetMapping("/api/community/board/post/{pId}")
    public ResponseEntity<PostResponseForm> board(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        postService.increasePostViewCount(pId, userEmail);

        try {
            PostResponseForm post = new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId)));

            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 회원 커뮤니티 게시글 추천 수를 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 추천 수를 반환합니다.
     */
    @GetMapping("/api/community/board/post/{pId}/vote/up")
    public ResponseEntity<String> getUpVotePost(@PathVariable Long pId) {
        return ResponseEntity.ok(postService.getIncreasePostVoteCount(pId));
    }


    /**
     * 회원 커뮤니티 게시글 비추천 수를 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 게시글 비추천 수를 반환합니다.
     */
    @GetMapping("/api/community/board/post/{pId}/vote/down")
    public ResponseEntity<String> getDownVotePost(@PathVariable Long pId) {
        return ResponseEntity.ok(postService.getDecreasePostVoteCount(pId));
    }


    /**
     * 회원 커뮤니티 게시글을 추천하는 메서드입니다.
     * 하루에 2번 이상 다시 추천/비추천할 수 없습니다. 자세한 사항은 postService 참조.
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 게시글 추천 수를 반환합니다.
     */
    @PostMapping("/api/community/board/post/{pId}/vote/up")
    public ResponseEntity<String> upVotePost(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();
        return ResponseEntity.ok(postService.increasePostVoteCount(pId, userEmail));
    }


    /**
     * 회원 커뮤니티 게시글을 비추천하는 메서드입니다.
     * 하루에 2번 이상 다시 추천/비추천할 수 없습니다. 자세한 사항은 postService 참조.
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 게시글 비추천 수를 반환합니다.
     */
    @PostMapping("/api/community/board/post/{pId}/vote/down")
    public ResponseEntity<String> downVotePost(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();
        return ResponseEntity.ok(postService.decreasePostVoteCount(pId, userEmail));
    }


    /**
     * 회원 커뮤니티 게시글을 새롭게 등록하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param form 등록 게시글 정보가 담긴 객체
     * @return 게시글을 저장하고 해당 게시글 상세 정보를 반환합니다.
     */
    @PostMapping("/api/community/board/post")
    public ResponseEntity<PostResponseForm> writePost(@RequestHeader(AUTHORIZATION) String token, @RequestBody PostWriteForm form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        try {
            Long pId = postService.savePost(form, userEmail);
            return ResponseEntity.ok(new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId))));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * (추후 수정 필요)
     *
     * 회원 커뮤니티 게시글을 수정할 수 있는 권한을 체크합니다.
     * 해당 게시글 작성자와 현재 클라이언트 로그인 정보가 같은 경우에만 수정할 수 있습니다.
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 게시글 권한이 확인되면 ok, 그렇지 않으면 BAD_REQUEST 발생.
     */
    @GetMapping("/api/community/board/post/{pId}")
    public ResponseEntity<String> updateBoardForm(@RequestHeader(AUTHORIZATION) String token,
                                                  @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        try {
            PostResponseForm post = new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId)));

            if (!userEmail.equals(post.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
            }

            return ResponseEntity.ok("ok");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }
    }

    /**
     * 회원 커뮤니티 게시판 게시글을 수정하는 메서드입니다.
     * <p><b>작성자만 수정이 가능합니다.</b></p>
     *
     * @param pId 게시글 ID
     * @param form 수정 사항이 담긴 DTO 객체
     * @return 수정된 게시글 상세 정보를 담아 반환합니다.
     */
    @PutMapping("/api/community/board/post/{pId}")
    public ResponseEntity<PostResponseForm> updateBoard(@RequestHeader(AUTHORIZATION) String token,
                                                      @PathVariable Long pId,
                                                      @RequestBody PostWriteForm form) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        try {
            if (!userEmail.equals(postService.getPost(pId).getMember().getEmail())) {
                throw new IllegalArgumentException("error");
            }

            return ResponseEntity.ok(postService.updatePost(pId, form));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 회원 커뮤니티 게시판 게시글을 삭제하는 메서드입니다.
     * <p><b>작성자만 삭제가 가능합니다.</b></p>
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @return 권한이 확인되었고 정상적으로 삭제된다면 ok, 이외의 경우에는 BAD_REQUEST 발생.
     */
    @DeleteMapping("/api/community/board/post/{pId}")
    public ResponseEntity<String> removeBoard(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        try {
            PostResponseForm post = new PostResponseForm(postService.getPost(pId), Long.parseLong(postService.getPostViewCount(pId)));

            if (!userEmail.equals(post.getEmail())) {
                throw new IllegalArgumentException("error");
            }

            postService.removePost(pId);

            return ResponseEntity.ok("removed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    /**
     * 게시글에 첨부된 문제를 저장하는 메서드입니다.
     * 사용자 별로 개별적으로 마이북에 문제가 저장됩니다.
     *
     * @param qId 게시글 ID
     * @return 게시글에 첨부된 문제가 정상적으로 저장되면 ok, 그렇지 않으면 BAD_REQUEST 반환.
     */
    @PostMapping("/api/community/board/post/{qId}/question")
    public ResponseEntity<String> saveQuestionFromPost(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long qId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        try {
            return ResponseEntity.ok(postService.saveQuestionFromPost(qId, userEmail).toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }
    }

    /**
     * 게시글에 문제를 첨부하기 위해, 나의 문제 목록을 보여주는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @return 회원이 저장한 모든 문제들의 리스트를 보여줍니다.
     */
    @GetMapping("/api/community/board/post/uploadQuestion")
    public ResponseEntity<BookResponseForm> getUserQuestions(@RequestHeader(AUTHORIZATION) String token) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        try {
            return ResponseEntity.ok(new BookResponseForm(bookService.findBook(userEmail)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * 첨부할 문제 ID를 요청하면, 첨부되는 문제 상세 정보를 반환합니다.
     *
     * @param qId 첨부할 문제 ID
     * @return 문제 상세 정보를 반환합니다.
     */
    @PostMapping("/api/community/board/post/uploadQuestion")
    public ResponseEntity<QuestionResponseForm> uploadUserQuestion(@RequestParam Long qId) {
        try {
            Question question = questionService.getQuestion(qId);
            return ResponseEntity.ok(QuestionResponseForm.builder()
                    .qId(question.getId())
                    .questionType(question.getType().getKrName())
                    .title(question.getTitle())
                    .mainText(question.getMainText())
                    .choices(question.getChoices().stream().map(Choice::getChoice).toList())
                    .shareCounter(question.getShareCounter())
                    .createdDate(question.getCreatedDate())
                    .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}