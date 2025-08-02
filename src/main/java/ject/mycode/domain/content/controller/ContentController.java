package ject.mycode.domain.content.controller;

import ject.mycode.domain.auth.jwt.annotation.CurrentUser;
import ject.mycode.domain.content.service.ContentService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	private final ContentServiceImpl contentServiceImpl;
	private final ContentService contentService;


	@PostMapping("/{contentId}/favorites")
	public BaseResponse<Long> addFavorite(@CurrentUser User user, @PathVariable Long contentId){
		return new BaseResponse<>(BaseResponseCode.ADD_FAVORITE, contentServiceImpl.addFavorite(user, contentId));
	}

	@GetMapping("/{contentId}")
	public BaseResponse<ContentDetailsRes> getContentDetails(@AuthenticationPrincipal User user,
		@PathVariable Long contentId) {
		return new BaseResponse<>(BaseResponseCode.GET_CONTENT_DETAILS,
			contentServiceImpl.getContentDetails(user, contentId));
	}
}
