package hpclab.kcsatspringcommunity.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    SIGN_UP_SUCCESS(HttpStatus.OK, "S001", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "S002", "로그인에 성공하였습니다."),
    POST_CREATED(HttpStatus.CREATED, "S003", "게시글이 등록되었습니다."),
    COMMENT_DELETE_SUCCESS(HttpStatus.OK, "S003", "댓글이 정상적으로 삭제되었습니다."),
    POST_DELETE_SUCCESS(HttpStatus.OK, "S004", "게시글이 정상적으로 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}