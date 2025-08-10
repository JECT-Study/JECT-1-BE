package ject.mycode.domain.survey.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SurveyAnswerReq {
	//TODO: 추천지역도 받고, 기존응답은 list로 한번에 받도록 변경
	private String region;
	private List<AnswerPair> answers;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class AnswerPair {
		private Long questionId;
		private Long optionId;
	}
}
