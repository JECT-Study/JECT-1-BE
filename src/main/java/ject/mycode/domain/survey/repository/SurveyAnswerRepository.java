package ject.mycode.domain.survey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.survey.entity.SurveyAnswer;
import ject.mycode.domain.user.entity.User;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
	Optional<SurveyAnswer> findByUserAndSurveyQuestion(User user, Long surveyQuestion);
}
