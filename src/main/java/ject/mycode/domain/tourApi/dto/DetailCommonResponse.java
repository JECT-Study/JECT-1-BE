package ject.mycode.domain.tourApi.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class DetailCommonResponse {
    private Wrapper response;

    @Getter
    public static class Wrapper {
        private Header header;
        private Body body;
    }

    @Getter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Getter
    public static class Items {
        private List<DetailItem> item;
    }

    @Getter
    public static class DetailItem {
        private String contentid;
        private String contenttypeid;
        private String title;

        // 주소
        private String addr1;
        private String addr2;
        private String zipcode;

        // 지역 코드
        private String areacode;
        private String sigungucode;

        // 좌표
        private String mapx;
        private String mapy;
        private String mlevel;

        // 이미지
        private String firstimage;
        private String firstimage2;

        // 개요, 홈페이지, 연락처
        private String overview;
        private String homepage;
        private String tel;

        // 기타
        private String createdtime;
        private String modifiedtime;
    }
}
