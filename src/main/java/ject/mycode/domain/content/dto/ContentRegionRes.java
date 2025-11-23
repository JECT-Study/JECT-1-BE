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
        String[] parts = address.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            sb.append(part).append(" ");
            if (part.endsWith("구") || part.endsWith("군")) {
                break;
            }
        }

        return sb.toString().trim();
    }
}


