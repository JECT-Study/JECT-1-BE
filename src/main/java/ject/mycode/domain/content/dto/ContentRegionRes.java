package ject.mycode.domain.content.dto;

import ject.mycode.domain.region.dto.RegionRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContentRegionRes {
    private Long contentId;
    private String title;
    private String address;
    private String imageUrl;
    private String startDate;
    private String endDate;
    private RegionRes region;
}


