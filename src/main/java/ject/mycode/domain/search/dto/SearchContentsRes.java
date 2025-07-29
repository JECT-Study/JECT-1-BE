package ject.mycode.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchContentsRes {
    private int totalCount;
    private int currentPage;
    private List<ContentSummary> contents;
}
