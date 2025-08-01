package ject.mycode.global.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseResponseCode {
	// 1000번대 : 성공코드
	LOGIN_SUCCESS(true, 1000, "로그인 성공", HttpStatus.OK),
	TOKEN_REISSUE_SUCCESS(true, 1001, "토큰 재발급이 완료되었습니다.", HttpStatus.OK),
	LOGOUT_SUCCESS(true, 1002, "로그아웃이 완료되었습니다.", HttpStatus.OK),


	// 1100 ~ 1199 : 컨텐츠 관련
	ADD_FAVORITE(true, 1100, "관심목록에 추가했습니다.", HttpStatus.CREATED),
	GET_CONTENT_DETAILS(true, 1101, "컨텐츠 상세페이지를 불러옵니다.", HttpStatus.OK),
	GET_RECOMMENDED_CONTENT(true, 1102, "맞춤 콘텐츠를 불러옵니다.", HttpStatus.OK),
	GET_HOT_CONTENT(true, 1103, "핫한 콘텐츠를 불러옵니다.", HttpStatus.OK),
	GET_WEEKLY_CONTENT(true, 1103, "금주 콘텐츠를 불러옵니다.", HttpStatus.OK),
	GET_SAME_CATEGORY_CONTENT(true, 1104, "카테고리별 콘텐츠를 불러옵니다.", HttpStatus.OK),

	// 1200 ~ 1299 : 일정 관련
	ADD_SCHEDULE_SUCCESS(true, 1200, "내 일정에 추가했습니다.", HttpStatus.CREATED),

	//1300 ~ 1399 : 마이페이지 관련
	GET_FAVORITES(true, 1300, "관심목록을 불러옵니다.", HttpStatus.OK),
	GET_MY_SCHEDULES(true, 1301, "나의 일정을 불러옵니다.", HttpStatus.OK),
	GET_SCHEDULED_DATES(true, 1302, "일정이 있는 날짜들을 불러옵니다.", HttpStatus.OK),

	//1400 ~ 1499 설문 관련
	SAVE_ANSWER_SUCCESS(true, 1400, "설문이 완료되었습니다.", HttpStatus.CREATED),

	// 1900 ~ 1999 기타
	SAVE_CONTENT_INFO(true, 1900, "컨텐츠 정보를 api로 불러와서 저장했습니다.", HttpStatus.CREATED),

	// 2000번대 : 클라이언트 오류

	// 2100 ~ 2199 : 컨텐츠 관련
	CONTENT_NOT_EXIST(false, 2100, "존재하지 않는 컨텐츠입니다.", HttpStatus.NOT_FOUND),

	//2200 ~ 2299 : 일정 관련

	VALIDATION_FAILED(false, 2999, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

	// 2300~2399 : 검색 관련
	SEARCH_SUCCESS(true, 2300, "컨텐츠를 검색합니다.", HttpStatus.OK),
	RECENT_SEARCH_SUCCESS(true, 2301, "최근 검색어 목록을 조회합니다.", HttpStatus.OK),
	DELETE_SUCCESS(true, 2302,"최근 검색어가 삭제되었습니다.", HttpStatus.OK),
	DELETE_ALL_SUCCESS(true, 2303,"최근 검색어가 모두 삭제되었습니다.", HttpStatus.OK),
	POPULAR_KEYWORD_SUCCESS(true, 2304,"인기 검색어를 조회합니다. (최대 10개)", HttpStatus.OK),
	SEARCH_RESULT_SUCCESS(true, 2305,"검색 결과 페이지를 조회합니다.", HttpStatus.OK),
	SEARCH_KEYWORD_MISSING(false, 2311, "검색어를 입력해 주세요.",  HttpStatus.NOT_FOUND),
	SEARCH_KEYWORD_NOT_FOUND(false, 2312, "검색어를 찾을 수 없습니다.",  HttpStatus.NOT_FOUND),

	// 3000번대 : 응답 오류

	// 4000번대 : 서버 내부 오류;
	INTERNAL_SERVER_ERROR(false, 4000, "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	;

	private final Boolean status;
	private final Integer code;
	private final String message;
	private final HttpStatus httpStatus;
}
