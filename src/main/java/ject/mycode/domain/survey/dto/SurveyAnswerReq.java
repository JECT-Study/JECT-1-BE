package ject.mycode.domain.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SurveyAnswerReq {
	private Long questionId;
	private Long optionId;
}
