package ject.mycode.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResultRes {
    private List<ContentResultRes> contentList;
    private PageInfoRes pageInfo;
}