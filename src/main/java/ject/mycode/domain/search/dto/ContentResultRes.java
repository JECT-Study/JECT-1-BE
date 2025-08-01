package ject.mycode.domain.search.dto;

import ject.mycode.domain.content.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentResultRes {
    private Long id;
    private String title;
    private ContentType category;
    private String address;
    private String thumbnailUrl;
}
