package ject.mycode.domain.home.controller;


import ject.mycode.domain.content.dto.ContentRecommendRes;
import ject.mycode.domain.content.dto.HotContentRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.service.ContentService;
import org.springframework.web.bind.annotation.*;

import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController{

    private final ContentService contentService;

    @GetMapping("/recommendations")
    public BaseResponse<List<ContentRecommendRes>> getRecommendedContents(
            @RequestParam(value = "category", defaultValue = "PERFORMANCE") ContentType contentType
    ) {
        return new BaseResponse<>(BaseResponseCode.GET_RECOMMENDED_CONTENT,
                contentService.getRecommendedContents(contentType));
    }

    @GetMapping("/festival/hot")
    public BaseResponse<List<HotContentRes>> getHotContents(
            @RequestParam(value = "category", defaultValue = "PERFORMANCE") ContentType contentType
    ) {
        return new BaseResponse<>(BaseResponseCode.GET_HOT_CONTENT,
                contentService.getHotContents(contentType));
    }
}

