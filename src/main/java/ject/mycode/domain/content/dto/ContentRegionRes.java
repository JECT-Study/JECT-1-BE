package ject.mycode.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ContentRegionRes {
    private Long contentId;
    private String title;
    private String image;
    private String address;
    private String startDate;
    private String endDate;

    public ContentRegionRes(Long contentId,
                            String title,
                            String image,
                            String address,
                            String startDate,
                            String endDate) {

        this.contentId = contentId;
        this.title = title;
        this.image = image;

        // 주소는 여기서 가공!
        this.address = extractGu(address);

        this.startDate = startDate;
        this.endDate = endDate;
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


