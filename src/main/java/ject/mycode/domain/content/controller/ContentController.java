package ject.mycode.domain.content.controller;

import ject.mycode.domain.content.dto.ContentRecommendRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.service.ContentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.service.ContentServiceImpl;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController{

	private final ContentServiceImpl contentServiceImpl;
	private final ContentService contentService;


	@PostMapping("/{contentId}/favorites")
	public BaseResponse<Long> addFavorite(@AuthenticationPrincipal User user, @PathVariable Long contentId){
		return new BaseResponse<>(BaseResponseCode.ADD_FAVORITE, contentServiceImpl.addFavorite(user, contentId));
	}

	@GetMapping("/{contentId}")
	public BaseResponse<ContentDetailsRes> getContentDetails(@PathVariable Long contentId){
		return new BaseResponse<>(BaseResponseCode.GET_CONTENT_DETAILS,
			contentServiceImpl.getContentDetails(contentId));
	}

	@GetMapping("/recommend")
	public BaseResponse<List<ContentRecommendRes>> getRecommendedContents(
			@RequestParam(value = "contentType", defaultValue = "PERFORMANCE") ContentType contentType
	) {
		return new BaseResponse<>(BaseResponseCode.GET_RECOMMENDED_CONTENT,
				contentService.getRecommendedContents(contentType));
	}
}
