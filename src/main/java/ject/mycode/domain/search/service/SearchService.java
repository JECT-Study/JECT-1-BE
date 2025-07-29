package ject.mycode.domain.search.service;

import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.search.dto.SearchContentsRes;
import ject.mycode.domain.search.dto.SearchResultRes;
import ject.mycode.domain.user.entity.User;

import java.util.List;

public interface SearchService {
    SearchContentsRes searchContents(String keyword, int page, int limit, String sort, User user);
    List<String> getRecentSearchKeywords(User user);
    void deleteKeyword(User user, String keyword);
    void deleteAllKeywords(User user);
    List<String> getPopularKeywords();
    SearchResultRes getSearchResults(String keyword, ContentType category, String region, int page, int size);
}

