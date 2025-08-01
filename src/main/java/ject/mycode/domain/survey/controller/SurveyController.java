package ject.mycode.domain.survey.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ject.mycode.domain.survey.dto.SurveyAnswerReq;
import ject.mycode.domain.survey.service.SurveyServiceImpl;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SurveyController {

	private final SurveyServiceImpl surveyService;

	@PostMapping("/trait-test")
	public BaseResponse<Void> saveTraitTestResult(@AuthenticationPrincipal User user,
		@RequestBody List<SurveyAnswerReq> answerDto) {
		surveyService.saveTraitTestResult(user, answerDto);
		return new BaseResponse<>(BaseResponseCode.SAVE_ANSWER_SUCCESS);
	}
}
