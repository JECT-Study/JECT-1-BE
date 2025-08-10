package ject.mycode.domain.user.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.exception.GlobalExceptionHandler;
import ject.mycode.global.response.BaseResponseCode;
import ject.mycode.global.response.ErrorResponseCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.custom.ContentQueryRepositoryImpl;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.s3.S3ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final ContentQueryRepositoryImpl contentQueryRepository;
	private final UserRepository userRepository;
	private final S3ImageService s3Service;

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
	public User getUserBySocialId(String SocialId) {
		return userRepository.findBySocialId(SocialId).orElseThrow(() -> new AuthHandler(ErrorResponseCode.USER_NOT_FOUND));
	}

	@Override
	@Transactional
	public void changeUserProfile(User user, MultipartFile image, String nickname) {
		// 1) 이미지 업로드 (있을 때만)
		String oldUrl = user.getImage();
		String newUrl = null;
		boolean uploadedNewImage = false;

		if (image != null && !image.isEmpty()) {
			// 1) 이미지 변경
			newUrl = s3Service.upload(image);
			uploadedNewImage = true;
			user.changeProfileImage(newUrl);
		}

		// 2) 닉네임 변경
		if (org.springframework.util.StringUtils.hasText(nickname)) {
			String trimmed = nickname.trim();

			// 현재 닉네임과 다른 경우에만 중복 검사
			if (!trimmed.equals(user.getNickname())
				&& userRepository.existsByNicknameAndIdNot(trimmed, user.getId())) {
				throw new CustomException(BaseResponseCode.DUPLICATED_NICKNAME);
			}
			user.changeNickname(trimmed);
		}

		// 4) "기존 이미지" 삭제 (신규 업로드가 있었고 URL이 바뀐 경우)
		if (uploadedNewImage && oldUrl != null && !Objects.equals(oldUrl, newUrl)) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					try { s3Service.deleteImageFromS3(oldUrl); }
					catch (Exception ex) { log.warn("커밋후 기존 이미지 삭제 실패: {}", oldUrl, ex); }
				}
			});
		}
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
