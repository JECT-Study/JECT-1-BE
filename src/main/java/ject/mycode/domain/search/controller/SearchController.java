package ject.mycode.domain.search.controller;

import ject.mycode.domain.search.dto.SearchContentsRes;
import ject.mycode.domain.search.service.SearchService;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public BaseResponse<SearchContentsRes> searchContents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new BaseResponse<>(BaseResponseCode.SEARCH_KEYWORD_MISSING);
        }

        SearchContentsRes result = searchService.searchContents(keyword, page, limit, sort);
        return new BaseResponse<>(BaseResponseCode.SEARCH_SUCCESS, result);
    }
}