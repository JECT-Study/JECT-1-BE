package ject.mycode.domain.tourApi.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class DetailIntroResponse {
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
		private List<IntroItem> item;
	}

	@Getter
	public static class IntroItem {
		private String contentid;
		private String contenttypeid;

		// 주최/주관
		private String sponsor1;
		private String sponsor1tel;
		private String sponsor2;
		private String sponsor2tel;

		// 행사 기간
		private String eventstartdate;
		private String eventenddate;

		// 장소/시간
		private String eventplace;
		private String playtime;

		// 홈페이지/예약
		private String eventhomepage;
		private String bookingplace;

		// 연령, 요금
		private String agelimit;
		private String usetimefestival;
		private String discountinfofestival;
		private String spendtimefestival;

		// 부대 정보
		private String subevent;
		private String program;
		private String placeinfo;

		// 등급/유형
		private String festivalgrade;
		private String progresstype;
		private String festivaltype;
	}
}
