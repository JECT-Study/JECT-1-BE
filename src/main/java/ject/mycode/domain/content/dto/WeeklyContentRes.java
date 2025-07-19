package ject.mycode.domain.content.dto;

import java.time.LocalDate;

public record WeeklyContentRes(
        Long contentId,
        String title,
        String image,
        String address,
        LocalDate startDate,
        LocalDate endDate
) {}

