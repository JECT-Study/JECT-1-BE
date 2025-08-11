package ject.mycode.domain.search.controller;

import ject.mycode.domain.auth.jwt.annotation.CurrentUser;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.search.dto.SearchContentsRes;
import ject.mycode.domain.search.dto.SearchResultRes;
import ject.mycode.domain.search.service.SearchService;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import ject.mycode.global.response.ErrorResponseCode;
import lombok.RequiredArgsConstructor;
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
            @CurrentUser User user
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new BaseResponse<>(BaseResponseCode.SEARCH_KEYWORD_MISSING);
        }

        SearchContentsRes result = searchService.searchContents(keyword, page, limit, sort, user);
        return new BaseResponse<>(BaseResponseCode.SEARCH_SUCCESS, result);
    }


    @GetMapping("/recent")
    public BaseResponse<List<String>> getRecentSearches(@CurrentUser User user) {
        List<String> recentKeywords = searchService.getRecentSearchKeywords(user);
        return new BaseResponse<>(BaseResponseCode.RECENT_SEARCH_SUCCESS, recentKeywords);
    }

    @DeleteMapping("/keywords/{keyword}")
    public BaseResponse<Void> deleteKeyword(@CurrentUser User user,
                                            @PathVariable String keyword) {
        searchService.deleteKeyword(user, keyword);
        return new BaseResponse<>(BaseResponseCode.DELETE_SUCCESS);
    }

    @DeleteMapping("/keywords")
    public BaseResponse<?> deleteAllKeywords(@CurrentUser User user) {
//        if (user == null) {
//            throw new CustomException(BaseResponseCode.USER_NOT_AUTHENTICATED);
//        }

        searchService.deleteAllKeywords(user);
        return new BaseResponse<>(BaseResponseCode.DELETE_ALL_SUCCESS);
    }

    @GetMapping("/popular")
    public BaseResponse<List<String>> getPopularKeywords() {
        List<String> popularKeywords = searchService.getPopularKeywords();
        return new BaseResponse<>(BaseResponseCode.POPULAR_KEYWORD_SUCCESS, popularKeywords);
    }

    @GetMapping("/results")
    public BaseResponse<SearchResultRes> searchContents(
            @RequestParam String keyword,
            @RequestParam(required = false) ContentType category,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SearchResultRes result = searchService.getSearchResults(keyword, category, region, page, size);
        return new BaseResponse<>(BaseResponseCode.SEARCH_RESULT_SUCCESS, result);
    }
}