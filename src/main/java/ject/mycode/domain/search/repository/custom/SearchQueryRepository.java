package ject.mycode.domain.search.repository.custom;

import ject.mycode.domain.content.entity.Content;

import java.util.List;

public interface SearchQueryRepository {
    List<Content> findContentsByKeyword(String keyword, int limit, int offset, String sort);
    int countContentsByKeyword(String keyword);
}