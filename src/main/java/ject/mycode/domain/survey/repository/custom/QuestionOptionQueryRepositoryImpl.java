package ject.mycode.domain.survey.repository.custom;

import static ject.mycode.domain.survey.dto.SurveyAnswerReq.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ject.mycode.domain.survey.dto.SurveyAnswerReq;
import ject.mycode.domain.survey.entity.QQuestionOption;
import ject.mycode.domain.survey.entity.QSurveyQuestion;
import ject.mycode.domain.survey.entity.QuestionOption;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionOptionQueryRepositoryImpl implements QuestionOptionQueryRepository {

	private final JPAQueryFactory qf;
	private final QQuestionOption questionOption = QQuestionOption.questionOption;
	private final QSurveyQuestion surveyQuestion = QSurveyQuestion.surveyQuestion;

	@Override
	public List<QuestionOption> findAllByQuestionIdAndOptionOrder(List<AnswerPair> answers) {
		BooleanBuilder condition = new BooleanBuilder();

		for (AnswerPair answer : answers) {
			condition.or(
				questionOption.surveyQuestion.id.eq(answer.getQuestionId())
					.and(questionOption.questionOptionOrder.eq(answer.getOptionId().intValue()))
			);
		}

		return qf.selectFrom(questionOption)
			.join(questionOption.surveyQuestion, surveyQuestion).fetchJoin()
			.where(condition)
			.fetch();
	}
}
