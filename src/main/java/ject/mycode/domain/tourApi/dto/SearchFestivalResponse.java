package ject.mycode.domain.tourApi.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class SearchFestivalResponse {
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
		private List<Item> item;
	}

	@Getter
	public static class Item {
		private String contentid;
		private String contenttypeid;
		private String title;

		// 주소
		private String addr1;
		private String addr2;

		// 지역 코드
		private String areacode;
		private String sigungucode;

		// 좌표
		private String mapx;
		private String mapy;

		// 이미지
		private String firstimage;
		private String firstimage2;

		// 카테고리
		private String cat1;
		private String cat2;
		private String cat3;

		// 축제 날짜
		private String eventstartdate;
		private String eventenddate;
	}
}
