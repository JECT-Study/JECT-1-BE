package ject.mycode.domain.survey.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.region.entity.Region;
import ject.mycode.domain.region.repository.RegionRepository;
import ject.mycode.domain.survey.dto.SurveyAnswerReq;
import ject.mycode.domain.survey.entity.QuestionOption;
import ject.mycode.domain.survey.entity.SurveyAnswer;
import ject.mycode.domain.survey.repository.QuestionOptionRepository;
import ject.mycode.domain.survey.repository.SurveyAnswerRepository;
import ject.mycode.domain.survey.repository.custom.QuestionOptionQueryRepositoryImpl;
import ject.mycode.domain.trait.entity.Trait;
import ject.mycode.domain.trait.entity.UserTrait;
import ject.mycode.domain.trait.repository.UserTraitRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

	private final QuestionOptionQueryRepositoryImpl questionOptionQueryRepository;
	private final RegionRepository regionRepository;
	private final SurveyAnswerRepository surveyAnswerRepository;
	private final UserTraitRepository userTraitRepository;
	private final QuestionOptionRepository questionOptionRepository;

	@Override
	@Transactional
	public void saveTraitTestResult(User user, SurveyAnswerReq answerDto) {
		List<QuestionOption> findOptionList = questionOptionQueryRepository
			.findAllByQuestionIdAndOptionOrder(answerDto.getAnswers());

		Map<Long, QuestionOption> questionOptionMap = findOptionList.stream()
			.collect(Collectors.toMap(
				qo -> qo.getSurveyQuestion().getId(),
				qo -> qo
			));

		for (SurveyAnswerReq.AnswerPair answer : answerDto.getAnswers()) {
			if (answer == null) {
				throw new IllegalArgumentException("해당 questionId/optionId에 매칭되는 QuestionOption이 없습니다: "
					+ answer.getQuestionId() + "/" + answer.getOptionId());
			}

			QuestionOption questionOption = questionOptionRepository.findById(answer.getOptionId())
				.orElseThrow(() -> new IllegalArgumentException("해당하는 질문 보기가 없습니다."));

			SurveyAnswer surveyAnswer = SurveyAnswer.builder()
				.user(user)
				.surveyQuestion(answer.getQuestionId())
				.questionOption(answer.getOptionId())
				.build();
			surveyAnswerRepository.save(surveyAnswer);

			UserTrait userTrait = UserTrait.builder()
				.user(user)
				.trait(Trait.builder().id(answer.getQuestionId()).build())
				.totalScore(questionOption.getScore())
				.build();
			userTraitRepository.save(userTrait);
		}

		Region findRegion = regionRepository.findByName(answerDto.getRegion())
			.orElseThrow(() -> new CustomException(BaseResponseCode.REGION_NOT_EXIST));

		user.saveRecommendRegion(findRegion);
	}

}
