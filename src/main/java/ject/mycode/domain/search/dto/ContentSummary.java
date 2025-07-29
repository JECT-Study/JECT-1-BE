package ject.mycode.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ContentSummary {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private String category;
    private String address;
    private LocalDate date;
    private int views;
}