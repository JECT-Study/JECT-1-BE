package ject.mycode.domain.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.survey.entity.QuestionOption;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
}
