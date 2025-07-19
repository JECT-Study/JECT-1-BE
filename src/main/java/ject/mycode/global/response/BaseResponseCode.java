package ject.mycode.global.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseResponseCode {
	// 1000번대 : 성공코드

	// 1100 ~ 1199 : 컨텐츠 관련
	ADD_FAVORITE(true, 1100, "관심목록에 추가했습니다.", HttpStatus.CREATED),
	GET_CONTENT_DETAILS(true, 1101, "컨텐츠 상세페이지를 불러옵니다.", HttpStatus.OK),
	GET_RECOMMENDED_CONTENT(true, 1102, "추천 콘텐츠를 불러옵니다.", HttpStatus.OK),

	// 1200 ~ 1299 : 일정 관련
	ADD_SCHEDULE_SUCCESS(true, 1200, "내 일정에 추가했습니다.", HttpStatus.CREATED),

	//1300 ~ 1399 : 마이페이지 관련
	GET_FAVORITES(true, 1300, "관심목록을 불러옵니다.", HttpStatus.OK),
	GET_MY_SCHEDULES(true, 1301, "나의 일정을 불러옵니다.", HttpStatus.OK),
	GET_SCHEDULED_DATES(true, 1302, "일정이 있는 날짜들을 불러옵니다.", HttpStatus.OK),

	// 2000번대 : 클라이언트 오류

	// 2100 ~ 2199 : 컨텐츠 관련
	CONTENT_NOT_EXIST(false, 2100, "존재하지 않는 컨텐츠입니다.", HttpStatus.NOT_FOUND),

	//2200 ~ 2299 : 일정 관련

	VALIDATION_FAILED(false, 2999, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

	// 3000번대 : 응답 오류

	// 4000번대 : 서버 내부 오류;
	INTERNAL_SERVER_ERROR(false, 4000, "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	;

	private final Boolean status;
	private final Integer code;
	private final String message;
	private final HttpStatus httpStatus;
}
