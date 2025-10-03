package ject.mycode.domain.survey.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SurveyAnswerReq {
	private List<String> regions;
	private List<AnswerPair> answers;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class AnswerPair {
		private Long questionId;
		private Long optionId;
	}
}
