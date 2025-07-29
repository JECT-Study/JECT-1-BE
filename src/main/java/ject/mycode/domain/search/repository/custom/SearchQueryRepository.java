package ject.mycode.domain.search.repository.custom;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.enums.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchQueryRepository {
    List<Content> findContentsByKeyword(String keyword, int limit, int offset, String sort);
    int countContentsByKeyword(String keyword);
    List<String> findTop10PopularKeywords();
    Page<Content> getSearchResults(String keyword, ContentType category, String region, Pageable pageable);
}