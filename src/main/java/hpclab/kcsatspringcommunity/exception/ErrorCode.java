package hpclab.kcsatspringcommunity.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E001", "입력값이 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E002", "사용자를 찾을 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "E003", "아이디 또는 비밀번호가 일치하지 않습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E004", "이미 존재하는 이메일입니다."),
    USER_VERIFICATION_FAILED(HttpStatus.FORBIDDEN, "E005", "사용자 인증에 실패하였습니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "존재하지 않는 댓글입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "E007", "존재하지 않는 게시글입니다."),
    MYBOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "E008", "존재하지 않는 마이북입니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "E009", "존재하지 않는 문제입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}