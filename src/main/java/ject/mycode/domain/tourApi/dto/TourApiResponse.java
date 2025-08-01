package ject.mycode.domain.tourApi.dto;

import lombok.Data;

import java.util.List;

@Data
public class TourApiResponse {
	private Response response;

	@Data
	public static class Response {
		private Header header;
		private Body body;
	}

	@Data
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Data
	public static class Body {
		private Items items;
		private int numOfRows;
		private int pageNo;
		private int totalCount;
	}

	@Data
	public static class Items {
		private List<FestivalItem> item;
	}

	@Data
	public static class FestivalItem {
		private String addr1;
		private String addr2;
		private String zipcode;
		private String cat1;
		private String cat2;
		private String cat3;
		private String contentid;
		private String contenttypeid;
		private String createdtime;
		private String eventstartdate;
		private String eventenddate;
		private String firstimage;
		private String firstimage2;
		private String mapx;
		private String mapy;
		private String modifiedtime;
		private String areacode;
		private String sigungucode;
		private String tel;
		private String title;
		private String overview;
		private String homepage;
	}
}
