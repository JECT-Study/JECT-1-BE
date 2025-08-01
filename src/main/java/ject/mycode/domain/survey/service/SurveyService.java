package ject.mycode.domain.survey.service;

import java.util.List;

import ject.mycode.domain.survey.dto.SurveyAnswerReq;
import ject.mycode.domain.user.entity.User;

public interface SurveyService {
	void saveTraitTestResult(User user, List<SurveyAnswerReq> answerDto);
}
