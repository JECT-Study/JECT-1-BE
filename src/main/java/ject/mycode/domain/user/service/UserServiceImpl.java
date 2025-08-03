package ject.mycode.domain.user.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.ErrorResponseCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.custom.ContentQueryRepositoryImpl;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final ContentQueryRepositoryImpl contentQueryRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<FavoritesRes> getUserFavorites(User user, ContentType contentType, Pageable pageable) {

		return contentQueryRepository.findFavoritesByUserId(user.getId(), contentType,
			pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SchedulesInfoRes> getMySchedules(User user, LocalDate day, Pageable pageable) {

		return contentQueryRepository.findMySchedulesByUserId(user.getId(), day,
			pageable);
	}

	@Override
	public List<LocalDate> getDaysWithSchedules(User user, YearMonth month) {
		LocalDate start = month.atDay(1);              
		LocalDate end = month.atEndOfMonth();

		return contentQueryRepository.findContentsByUserIdAndDateRange(user.getId(), start, end)
			.stream()
			.distinct()
			.sorted()
			.toList();
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new AuthHandler(ErrorResponseCode.USER_NOT_FOUND));
	}

//	@Transactional
//	public Long createUser(KakaoUserRes userResponse) {
//		String email = userResponse.kakaoAccount().email();
//
//		Optional<User> existingUser = userRepository.findByEmail(email);
//
//		if (existingUser.isPresent()) {
//			return existingUser.get().getId();
//		}
//
//		User newUser = User.builder()
//				.email(email)
//				.nickname(userResponse.kakaoAccount().profile().nickname())
//				// 필요한 필드들 추가 설정
//				.build();
//
//		User savedUser = userRepository.save(newUser);
//
//		return savedUser.getId();
//	}
//
//	@Override
//	public void saveUserIfNotExists(KakaoUserRes kakaoUserRes) {
//		String kakaoId = String.valueOf(kakaoUserRes.id());
//
//		Optional<User> existingUser = userRepository.findBySocialId(kakaoUserRes.id().toString());
//		if (existingUser.isEmpty()) {
//			KakaoAccount kakaoAccount = kakaoUserRes.kakaoAccount();
//			String nickname = NicknameGenerator.generate();
//			String email = kakaoAccount.email(); // null일 수도 있음
//
//			User newUser = User.builder()
//					.socialId(kakaoId)
//					.nickname(nickname)
//					.email(email)
//					.role(UserRole.NORMAL)
//					.socialType(SocialType.KAKAO)
//					.build();
//
//			userRepository.save(newUser);
//		}
//	}
}
