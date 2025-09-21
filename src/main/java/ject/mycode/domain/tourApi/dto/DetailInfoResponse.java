package ject.mycode.domain.tourApi.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class DetailInfoResponse {
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
		private List<InfoItem> item;
	}

	@Getter
	public static class InfoItem {
		private String contentid;
		private String contenttypeid;
		private String serialnum;
		private String infoname;
		private String infotext;
		private String fldgubun;
	}
}
