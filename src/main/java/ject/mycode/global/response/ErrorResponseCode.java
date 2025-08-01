package ject.mycode.global.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorResponseCode {

    // 2000번대 : 클라이언트 오류

    // 2100 ~ 2199 : 컨텐츠 관련
    CONTENT_NOT_EXIST(false, 2100, "존재하지 않는 컨텐츠입니다.", HttpStatus.NOT_FOUND),

    // 2200 ~ 2299 : 일정 관련

    VALIDATION_FAILED(false, 2999, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

    // 2300~2399 : 검색 관련
    SEARCH_KEYWORD_MISSING(false, 2311, "검색어를 입력해 주세요.", HttpStatus.NOT_FOUND),
    SEARCH_KEYWORD_NOT_FOUND(false, 2312, "검색어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 2400~2499 : 인증 관련
    PASSWORD_NOT_EQUAL(false, 2400, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_LOGGED_OUT(false, 2401, "로그아웃된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(false, 2402, "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNSUPPORTED_PROVIDER(false, 2403, "지원하지 않는 소셜 provider입니다", HttpStatus.BAD_REQUEST),

    // 4000번대 : 서버 내부 오류;
    INTERNAL_SERVER_ERROR(false, 4000, "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),// 예: 인증 관련 에러 코드가 있는 enum에 추가
    INVALID_TOKEN(false, 4004, "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(false, 4030, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(false, 4010, "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(false, 4011, "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final Boolean status;
    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}