package ject.mycode.domain.content.dto;

import java.time.LocalDate;

public record ContentCategoryRes(
        Long contentId,
        String title,
        String image,
        Double longitude,
        Double latitude,
        LocalDate startDate,
        LocalDate endDate,
        String address
) {

    public ContentCategoryRes {
        address = extractGu(address);
    }

    private String extractGu(String address) {
        if (address == null || address.isBlank()) {
            return address;
        }

        String[] parts = address.split(" ");

        if (parts.length >= 2) {
            return parts[0] + " " + parts[1];
        }
        return address;
    }
}
