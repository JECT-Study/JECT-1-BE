package ject.mycode.domain.survey.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.region.entity.Region;
import ject.mycode.domain.region.entity.UserRegion;
import ject.mycode.domain.region.repository.RegionRepository;
import ject.mycode.domain.region.repository.UserRegionRepository;
import ject.mycode.domain.survey.dto.SurveyAnswerReq;
import ject.mycode.domain.survey.entity.QuestionOption;
import ject.mycode.domain.survey.entity.SurveyAnswer;
import ject.mycode.domain.survey.repository.QuestionOptionRepository;
import ject.mycode.domain.survey.repository.SurveyAnswerRepository;
import ject.mycode.domain.trait.entity.UserTrait;
import ject.mycode.domain.trait.repository.UserTraitRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

	private final RegionRepository regionRepository;
	private final SurveyAnswerRepository surveyAnswerRepository;
	private final UserTraitRepository userTraitRepository;
	private final QuestionOptionRepository questionOptionRepository;
	private final UserRegionRepository userRegionRepository;

	@Override
	@Transactional
	public void saveTraitTestResult(User user, SurveyAnswerReq answerDto) {

		// 답변 처리
		for (SurveyAnswerReq.AnswerPair answer : answerDto.getAnswers()) {
			if (answer == null) {
				log.info("오류 로그 : option Id : {} , question Id : {}",
					answer.getOptionId(), answer.getQuestionId());
				throw new CustomException(BaseResponseCode.QUESTION_OPTION_ID_NOT_EXIST);
			}

			QuestionOption questionOption = questionOptionRepository.findById(answer.getOptionId())
				.orElseThrow(() -> new CustomException(BaseResponseCode.QUESTION_OPTION_ID_NOT_EXIST));

			// SurveyAnswer upsert
			SurveyAnswer surveyAnswer = surveyAnswerRepository
				.findByUserAndSurveyQuestion(user, answer.getQuestionId())
				.orElse(SurveyAnswer.builder()
					.user(user)
					.surveyQuestion(answer.getQuestionId())
					.build());

			surveyAnswer.changeAnswer(answer.getOptionId()); // 도메인 메서드
			surveyAnswerRepository.save(surveyAnswer);

			// UserTrait upsert
			Long traitId = questionOption.getSurveyQuestion().getTrait().getId();
			UserTrait userTrait = userTraitRepository
				.findByUserAndTraitId(user, traitId)
				.orElse(UserTrait.builder()
					.user(user)
					.trait(questionOption.getSurveyQuestion().getTrait())
					.totalScore(questionOption.getScore())
					.build());

			// 이미 존재한다면 점수 업데이트
			userTrait.updateTotalScore(questionOption.getScore());
			userTraitRepository.save(userTrait);
		}

		// 지역 다중선택 처리 (upsert)
		List<Region> findRegions = regionRepository.findByNameIn(answerDto.getRegions());

		// 기존 지역 삭제 후 새로 추가
		userRegionRepository.deleteByUser(user);
		for (Region region : findRegions) {
			UserRegion userRegion = UserRegion.builder()
				.user(user)
				.region(region)
				.build();
			userRegionRepository.save(userRegion);
		}
	}
}
