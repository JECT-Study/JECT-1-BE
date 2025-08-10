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
import ject.mycode.domain.survey.repository.custom.QuestionOptionQueryRepositoryImpl;
import ject.mycode.domain.trait.entity.Trait;
import ject.mycode.domain.trait.entity.UserTrait;
import ject.mycode.domain.trait.repository.TraitRepository;
import ject.mycode.domain.trait.repository.UserTraitRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

	private final QuestionOptionQueryRepositoryImpl questionOptionQueryRepository;
	private final TraitRepository traitRepository;
	private final UserTraitRepository userTraitRepository;
	private final RegionRepository regionRepository;

	@Override
	@Transactional
	public void saveTraitTestResult(User user, SurveyAnswerReq answerDto) {
		List<QuestionOption> findOptionList = questionOptionQueryRepository.findAllByQuestionIdAndOptionOrder(
			answerDto.getAnswers());

		Map<Long, QuestionOption> questionOptionMap = findOptionList.stream()
			.collect(Collectors.toMap(
				qo -> qo.getSurveyQuestion().getId(),
				qo -> qo
			));

		List<Trait> traitList = traitRepository.findAll();

		Map<Long, Trait> traitMap = traitList.stream()
			.collect(Collectors.toMap(
				Trait::getId,
				trait -> trait
			));

		for (SurveyAnswerReq.AnswerPair surveyAnswerReq : answerDto.getAnswers()) {
			Long questionId = surveyAnswerReq.getQuestionId();

			QuestionOption questionOption = questionOptionMap.get(questionId);
			Trait trait = traitMap.get(questionId);

			if (questionOption == null || trait == null) {
				throw new IllegalArgumentException("questionId에 해당하는 데이터가 없습니다: " + questionId);
			}

			UserTrait userTrait = UserTrait.builder()
				.trait(trait)
				.user(user)
				.totalScore(questionOption.getScore())
				.build();

			userTraitRepository.save(userTrait);
		}

		// 사용자 지역 등록
		Region findRegion = regionRepository.findByName(answerDto.getRegion())
			.orElseThrow(() -> new CustomException(BaseResponseCode.REGION_NOT_EXIST));

		user.saveRecommendRegion(findRegion);
	}
}
