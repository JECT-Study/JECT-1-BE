package ject.mycode.domain.content.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ject.mycode.domain.content.service.ContentServiceImpl;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ContentController{

	private final ContentServiceImpl contentServiceImpl;

	@PostMapping("/contents/{contentId}/favorites")
	public BaseResponse<Long> addFavorite(@AuthenticationPrincipal User user, @PathVariable Long contentId){
		return new BaseResponse<>(BaseResponseCode.ADD_FAVORITE, contentServiceImpl.addFavorite(user, contentId));
	}
}
