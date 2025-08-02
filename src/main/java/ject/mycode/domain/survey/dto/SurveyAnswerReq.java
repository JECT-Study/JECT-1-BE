package ject.mycode.domain.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SurveyAnswerReq {
	//TODO: 추천지역도 받고, 응답은 list로 한번에 받도록 변경
	private Long questionId;
	private Long optionId;
}
