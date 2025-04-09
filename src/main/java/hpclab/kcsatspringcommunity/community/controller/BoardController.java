package hpclab.kcsatspringcommunity.community.controller;

import hpclab.kcsatspringcommunity.JWTUtil;
import hpclab.kcsatspringcommunity.community.dto.*;
import hpclab.kcsatspringcommunity.community.service.CommentService;
import hpclab.kcsatspringcommunity.community.service.PostService;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardController {

    /**
     * 기능 구현 :
     * 1. 게시판 나타내기
     * 2. 게시글 검색
     * 3. 게시글 누르면 게시글 항목 표시
     * 4. 게시글 작성
     * 5. 댓글 작성
     * 6. 좋아요, 스크랩 버튼
     */

    private final PostService postService;
    private final CommentService commentService;
    private final QuestionJPARepository questionJPARepository;
    private final BookService bookService;
    private final JWTUtil jwtUtil;


    // 게시글 목록 페이지로 조회
    @GetMapping("/api/community/open/board")
    public ResponseEntity<Page<PostResponseForm>> getPostListByPage(@RequestParam(defaultValue = "0") int page,  // 페이지 번호 (기본값 0)
                                                                    @RequestParam(defaultValue = "10") int size,  // 페이지 크기 (기본값 10)
                                                                    @RequestParam(defaultValue = "pId,DESC") String sort,  // 정렬 기준 (기본값 pId 내림차순)
                                                                    @RequestParam(required = false) String keyword,  // 검색어 (선택적 파라미터)
                                                                    @RequestParam(required = false) QuestionType type  // 타입 필터 (선택적 파라미터)
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

    // 핫게 게시글 목록 페이지로 조회
    @GetMapping("/api/community/open/board/hot")
    public ResponseEntity<Page<PostResponseForm>> hotBoard(@RequestParam(defaultValue = "0") int page,  // 페이지 번호 (기본값 0)
                                                           @RequestParam(defaultValue = "10") int size,  // 페이지 크기 (기본값 10)
                                                           @RequestParam(defaultValue = "pId,DESC") String sort,  // 정렬 기준 (기본값 pId 내림차순)
                                                           @RequestParam(required = false) String keyword,  // 검색어 (선택적 파라미터)
                                                           @RequestParam(required = false) QuestionType type  // 타입 필터 (선택적 파라미터)
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

    // 게시글 상세 정보 조회
    @GetMapping("/api/community/board/post/{pId}")
    public ResponseEntity<PostDetailForm> board(@RequestHeader("Authorization") String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        postService.increasePostViewCount(pId, userEmail);
        PostDetailForm post = postService.getPost(pId);

        List<CommentResponseForm> hotComments = commentService.getHotComments(pId);
        post.setHotComments(hotComments);

        List<String> hotCommentsUpVoteCounter = new ArrayList<>();
        List<String> hotCommentsDownVoteCounter = new ArrayList<>();
        List<String> commentsUpVoteCounter = new ArrayList<>();
        List<String> commentsDownVoteCounter = new ArrayList<>();

        for (CommentResponseForm comment : hotComments) {
            String commentUpVoteCount = commentService.getIncreaseCommentCount(comment.getCId());
            hotCommentsUpVoteCounter.add(commentUpVoteCount);

            String commentDownVoteCount = commentService.getDecreaseCommentCount(comment.getCId());
            hotCommentsDownVoteCounter.add(commentDownVoteCount);
        }

        for (CommentResponseForm comment : post.getComments()) {
            String commentUpVoteCount = commentService.getIncreaseCommentCount(comment.getCId());
            commentsUpVoteCounter.add(commentUpVoteCount);

            String commentDownVoteCount = commentService.getDecreaseCommentCount(comment.getCId());
            commentsDownVoteCounter.add(commentDownVoteCount);
        }

        post.setHotCommentsUpVoteCounter(hotCommentsUpVoteCounter);
        post.setHotCommentsDownVoteCounter(hotCommentsDownVoteCounter);
        post.setCommentsUpVoteCounter(commentsUpVoteCounter);
        post.setCommentsDownVoteCounter(commentsDownVoteCounter);

        return ResponseEntity.ok(post);
    }


    // 게시글 추천 조회
    @GetMapping("/api/community/board/post/{pId}/postUpVote")
    public ResponseEntity<String> getUpVotePost(@PathVariable Long pId) {
        return ResponseEntity.ok(postService.getIncreasePostVoteCount(pId));
    }


    // 게시글 비추천 조회
    @GetMapping("/api/community/board/post/{pId}/postDownVote")
    public ResponseEntity<String> getDownVotePost(@PathVariable Long pId) {
        return ResponseEntity.ok(postService.getDecreasePostVoteCount(pId));
    }


    // 게시글 추천
    @PostMapping("/api/community/board/post/{pId}/postUpVote")
    public ResponseEntity<String> upVotePost(@RequestHeader("Authorization") String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();
        return ResponseEntity.ok(postService.increasePostVoteCount(pId, userEmail));
    }


    // 게시글 비추천
    @PostMapping("/api/community/board/post/{pId}/postDownVote")
    public ResponseEntity<String> downVotePost(@RequestHeader("Authorization") String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();
        return ResponseEntity.ok(postService.decreasePostVoteCount(pId, userEmail));
    }


    // 게시글 작성
    @PostMapping("/api/community/board/post/new")
    public ResponseEntity<PostDetailForm> writePost(@RequestHeader("Authorization") String token, @RequestBody PostWriteForm form) {

        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();
        log.info("userEmail : {}", userEmail);
        log.info("form : {}", form.toString());

        Long pId = postService.savePost(form, userEmail);

        return ResponseEntity.ok(postService.getPost(pId));
    }


    // 게시글 수정
    @GetMapping("/api/community/board/post/{pId}/update")
    public ResponseEntity<String> updateBoardFormAuth(@RequestHeader("Authorization") String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        PostDetailForm post = postService.getPost(pId);

        if (!Objects.equals(userEmail, post.getPost().getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        return ResponseEntity.ok("ok");
    }


    // 게시글 수정
    @PutMapping("/api/community/board/post/{pId}/update")
    public ResponseEntity<PostDetailForm> updateBoard(@PathVariable Long pId, @RequestBody PostWriteForm form) {
        return ResponseEntity.ok(postService.updatePost(pId, form));
    }


    // 게시글 삭제
    @DeleteMapping("/api/community/board/post/{pId}/remove")
    public ResponseEntity<String> removeBoardAuth(@RequestHeader("Authorization") String token, @PathVariable Long pId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        PostDetailForm post = postService.getPost(pId);

        if (!Objects.equals(userEmail, post.getPost().getEmail())) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        postService.removePost(pId);

        return ResponseEntity.ok("ok");
    }


    /**
     *
     * @param qId Post ID
     * @return Post에 걸려있던 Question 저장.
     */
    @PostMapping("/api/community/board/post/{qId}/save")
    public ResponseEntity<String> saveQuestionFromPost(@RequestHeader("Authorization") String token, @PathVariable Long qId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        if (!postService.saveQuestionFromPost(qId, userEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }

        return ResponseEntity.ok("ok");
    }


    // 업로드 할 문제 조회
    @GetMapping("/api/community/board/post/myQuestions")
    public ResponseEntity<BookResponseForm> getUserQuestions(@RequestHeader("Authorization") String token) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();
        return ResponseEntity.ok(bookService.findBook(userEmail));
    }


    // 업로드 할 문제 선택
    @PostMapping("/api/community/board/post/uploadQuestion")
    public ResponseEntity<QuestionResponseForm> uploadUserQuestion(@RequestParam Long qId) {

        Question question = questionJPARepository.findById(qId).orElseThrow(() -> new UsernameNotFoundException("찾는 문제가 존재하지 않습니다."));

        return ResponseEntity.ok(QuestionResponseForm.builder()
                .qId(question.getId())
                .questionType(question.getType().getKrName())
                .title(question.getTitle())
                .mainText(question.getMainText())
                .choices(question.getChoices().stream().map(Choice::getChoice).toList())
                .shareCounter(question.getShareCounter())
                .createdDate(question.getCreatedDate())
                .build());
    }


    // 댓글 작성
    @PostMapping("/api/community/board/post/{pId}/comment")
    public ResponseEntity<String> writeComment(@RequestHeader("Authorization") String token, @PathVariable Long pId, @RequestBody CommentWriteForm form) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        Long cId = commentService.writeComment(form, pId, userEmail);

        commentService.setCommentCount(cId);

        return ResponseEntity.ok("ok");
    }

    // 댓글 추천
    @PostMapping("/api/community/board/post/{pId}/comment/{cId}/commentUpVote")
    public ResponseEntity<String> upVoteComment(@RequestHeader("Authorization") String token, @PathVariable Long pId, @PathVariable Long cId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        String commentCount = commentService.increaseCommentCount(cId, userEmail);

        return ResponseEntity.ok(commentCount);
    }

    // 댓글 비추천
    @PostMapping("/api/community/board/post/{pId}/comment/{cId}/commentDownVote")
    public ResponseEntity<String> downVoteComment(@RequestHeader("Authorization") String token, @PathVariable Long pId, @PathVariable Long cId) {
        String userEmail = jwtUtil.getClaims(token).get("userEmail").toString();

        String commentCount = commentService.decreaseCommentCount(cId, userEmail);

        return ResponseEntity.ok(commentCount);
    }

    // 댓글 삭제
    @DeleteMapping("/api/community/board/post/{pId}/comment/{commentId}/remove")
    public ResponseEntity<String> removeComment(@PathVariable Long pId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.ok("ok");
    }
}