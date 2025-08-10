package ject.mycode.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContentRegionRes {
    private Long contentId;
    private String title;
    private String image;
    private String address;
    private String startDate;
    private String endDate;
}


