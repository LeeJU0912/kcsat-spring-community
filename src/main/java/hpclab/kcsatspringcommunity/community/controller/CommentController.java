package hpclab.kcsatspringcommunity.community.controller;

import hpclab.kcsatspringcommunity.JWTUtil;
import hpclab.kcsatspringcommunity.community.dto.CommentDetailForm;
import hpclab.kcsatspringcommunity.community.dto.CommentWriteForm;
import hpclab.kcsatspringcommunity.community.service.CommentService;
import hpclab.kcsatspringcommunity.exception.ApiResponse;
import hpclab.kcsatspringcommunity.exception.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hpclab.kcsatspringcommunity.JWTUtil.AUTHORIZATION;
import static hpclab.kcsatspringcommunity.JWTUtil.USER_EMAIL;
import static hpclab.kcsatspringcommunity.exception.SuccessCode.COMMENT_DELETE_SUCCESS;

/**
 * <p>회원 커뮤니티 댓글 컨트롤러 클래스입니다.</p>
 *
 * <p>기능 목록</p>
 * <ul>
 *     <li>댓글 작성/삭제</li>
 *     <li>댓글 추천/비추천</li>
 *     <li>댓글, 인기 댓글 조회</li>
 * </ul>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final JWTUtil jwtUtil;

    /**
     * 회원 커뮤니티 게시판 댓글 정보를 상세 조회하는 메서드입니다.
     *
     * @param pId 게시글 ID
     * @return 댓글 상세 정보를 반환합니다.
     */
    @GetMapping("/api/community/board/post/{pId}/comment")
    public ResponseEntity<ApiResponse<CommentDetailForm>> getComment(@PathVariable Long pId) {
        return ResponseEntity.ok(new ApiResponse<>(true, commentService.getAllComments(pId), null, null));
    }

    /**
     * 회원 커뮤니티 게시판 게시글에 댓글을 작성하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param pId 게시글 ID
     * @param form 댓글 본문이 작성된 Form DTO
     * @return 댓글 등록이 잘 되었다면 ok를 반환합니다.
     */
    @PostMapping("/api/community/board/post/{pId}/comment")
    public ResponseEntity<ApiResponse<String>> writeComment(@RequestHeader(AUTHORIZATION) String token,
                                               @PathVariable Long pId,
                                               @RequestBody CommentWriteForm form) {

        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        Long cId = commentService.writeComment(form, pId, userEmail);

        return ResponseEntity.ok(new ApiResponse<>(true, commentService.setCommentCount(cId), null, null));
    }

    /**
     * 회원 커뮤니티 게시판 게시글에 댓글을 추천하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param cId 댓글 ID
     * @return 추천한 댓글의 현재 추천수를 반환합니다.
     */
    @PostMapping("/api/community/board/comment/{cId}/vote/up")
    public ResponseEntity<ApiResponse<String>> upVoteComment(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long cId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        String commentCount = commentService.increaseCommentCount(cId, userEmail);

        return ResponseEntity.ok(new ApiResponse<>(true, commentCount, null, null));
    }

    /**
     * 회원 커뮤니티 게시판 게시글에 댓글을 비추천하는 메서드입니다.
     *
     * @param token 회원 JWT 토큰
     * @param cId 댓글 ID
     * @return 추천한 댓글의 현재 비추천수를 반환합니다.
     */
    @PostMapping("/api/community/board/comment/{cId}/vote/down")
    public ResponseEntity<ApiResponse<String>> downVoteComment(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long cId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        String commentCount = commentService.decreaseCommentCount(cId, userEmail);

        return ResponseEntity.ok(new ApiResponse<>(true, commentCount, null, null));
    }

    /**
     * 회원 커뮤니티 게시판 게시글에 작성된 댓글은 삭제하는 메서드입니다.
     * <p><b>댓글 작성자 본인만 삭제가 가능합니다.</b></p>
     *
     * @param token 회원 JWT 토큰
     * @param cId 댓글 ID
     * @return 댓글 삭제가 정상적으로 되었다면 ok, 그렇지 않다면 BAD_REQUEST 반환.
     */
    @DeleteMapping("/api/community/board/comment/{cId}")
    public ResponseEntity<ApiResponse<Void>> removeComment(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long cId) {
        String userEmail = jwtUtil.getClaims(token).get(USER_EMAIL).toString();

        commentService.checkCommentWriter(userEmail, cId);
        commentService.deleteComment(cId);

        return ResponseEntity.ok(new ApiResponse<>(true, null, COMMENT_DELETE_SUCCESS.getCode(), COMMENT_DELETE_SUCCESS.getMessage()));
    }
}