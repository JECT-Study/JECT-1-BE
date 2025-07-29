package ject.mycode.domain.search.controller;

import ject.mycode.domain.search.dto.SearchContentsRes;
import ject.mycode.domain.search.service.SearchService;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @AuthenticationPrincipal User user
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new BaseResponse<>(BaseResponseCode.SEARCH_KEYWORD_MISSING);
        }

        SearchContentsRes result = searchService.searchContents(keyword, page, limit, sort, user);
        return new BaseResponse<>(BaseResponseCode.SEARCH_SUCCESS, result);
    }


    @GetMapping("/recent")
    public BaseResponse<List<String>> getRecentSearches(@AuthenticationPrincipal User user) {
        List<String> recentKeywords = searchService.getRecentSearchKeywords(user);
        return new BaseResponse<>(BaseResponseCode.SEARCH_SUCCESS, recentKeywords);
    }

    @DeleteMapping("/keywords/{keyword}")
    public BaseResponse<Void> deleteKeyword(@AuthenticationPrincipal User user,
                                            @PathVariable String keyword) {
        searchService.deleteKeyword(user, keyword);
        return new BaseResponse<>(BaseResponseCode.DELETE_SUCCESS);
    }
}