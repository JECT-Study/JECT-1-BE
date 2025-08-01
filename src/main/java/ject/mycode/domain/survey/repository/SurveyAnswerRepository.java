package ject.mycode.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.survey.entity.SurveyAnswer;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
}
