package ject.mycode.domain.content.controller;

import ject.mycode.domain.auth.jwt.annotation.CurrentUser;
import ject.mycode.domain.content.dto.AddLikeRes;

import org.springframework.web.bind.annotation.*;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.service.ContentServiceImpl;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController{

	private final ContentServiceImpl contentService;

	@PostMapping("/{contentId}/favorites")
	public BaseResponse<AddLikeRes> addFavorite(@CurrentUser User user, @PathVariable Long contentId){
		return new BaseResponse<>(BaseResponseCode.ADD_FAVORITE, contentService.addFavorite(user, contentId));
	}

	@DeleteMapping("/{contentId}/favorites")
	public BaseResponse<Long> removeFavorite(@CurrentUser User user, @PathVariable Long contentId){
		return new BaseResponse<>(BaseResponseCode.REMOVE_FAVORITE, contentService.deleteFavorite(user, contentId));
	}

	@GetMapping("/{contentId}")
	public BaseResponse<ContentDetailsRes> getContentDetails(@CurrentUser User user,
		@PathVariable Long contentId) {
		return new BaseResponse<>(BaseResponseCode.GET_CONTENT_DETAILS,
			contentService.getContentDetails(user, contentId));
	}
}
