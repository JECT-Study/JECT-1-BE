package ject.mycode.domain.content.service;

import java.time.LocalDate;
import java.util.List;

import ject.mycode.domain.content.dto.*;
import ject.mycode.domain.content.enums.ContentType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.content.repository.custom.ContentQueryRepositoryImpl;
import ject.mycode.domain.contentImage.entity.ContentImage;
import ject.mycode.domain.contentImage.repository.ContentImageRepository;
import ject.mycode.domain.favorite.entity.Favorite;
import ject.mycode.domain.favorite.repository.FavoriteRepository;
import ject.mycode.domain.tag.repository.contentTagRepo.ContentTagRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

	private final ContentRepository contentRepository;
	private final FavoriteRepository favoriteRepository;
	private final ContentQueryRepositoryImpl contentQueryRepository;
	private final ContentImageRepository contentImageRepository;
	private final ContentTagRepository contentTagRepository;

	@Override
	@Transactional
	public Long addFavorite(User user, Long contentId) {
		Content findContent = contentRepository.findById(contentId)
			.orElseThrow(() -> new CustomException(BaseResponseCode.CONTENT_NOT_EXIST));

		Favorite favorite = Favorite.builder()
			.content(findContent)
			.user(user)
			.build();

		return favoriteRepository.save(favorite).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public ContentDetailsRes getContentDetails(Long contentId) {
		ContentDetailsRes findDetailsDto = contentQueryRepository.findDetailsByContentId(contentId);

		if(findDetailsDto == null) {
			throw new CustomException(BaseResponseCode.CONTENT_NOT_EXIST);
		}

		List<String> findImageList = contentImageRepository.findAllByContentId(contentId)
			.stream()
			.map(ContentImage::getImageUrl)
			.toList();

		List<String> findTagList = contentTagRepository.findAllByContentId(contentId)
			.stream()
			.map(ct -> ct.getTag().getName())
			.toList();

		findDetailsDto.setImages(findImageList);
		findDetailsDto.setTags(findTagList);

		return findDetailsDto;
	}

	@Override
	public List<ContentRecommendRes> getRecommendedContents(ContentType contentType) {
		return contentQueryRepository.findRecommendedContents(contentType);
	}

	@Override
	public List<HotContentRes> getHotContents(ContentType contentType) {
		return contentQueryRepository.findHotContentsThisMonth(contentType);
	}

	@Override
	public List<WeeklyContentRes> getContentsByDate(LocalDate date) {
		return contentQueryRepository.findContentsByDate(date);
	}

	@Override
	public List<ContentCategoryRes> getSameCategoryContents(ContentType contentType) {
		return contentQueryRepository.findContentsByCategory(contentType);
	}
}
