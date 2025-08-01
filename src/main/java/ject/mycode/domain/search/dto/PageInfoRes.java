package ject.mycode.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoRes {
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
