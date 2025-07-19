package ject.mycode.domain.content.dto;

import java.time.LocalDate;

public record ContentCategoryRes(
        Long contentId,
        String title,
        String image,
        Double longitude,
        Double latitude,
        LocalDate startDate,
        LocalDate endDate
) {}
