package ject.mycode.domain.survey.repository.custom;

import static ject.mycode.domain.survey.dto.SurveyAnswerReq.*;

import java.util.List;

import ject.mycode.domain.survey.dto.SurveyAnswerReq;
import ject.mycode.domain.survey.entity.QuestionOption;

public interface QuestionOptionQueryRepository {
	List<QuestionOption> findAllByQuestionIdAndOptionOrder(List<AnswerPair> answers);
}
