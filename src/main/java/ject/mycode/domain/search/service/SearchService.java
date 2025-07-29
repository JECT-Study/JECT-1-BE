package ject.mycode.domain.search.service;

import ject.mycode.domain.search.dto.SearchContentsRes;

public interface SearchService {
    SearchContentsRes searchContents(String keyword, int page, int limit, String sort);
}

