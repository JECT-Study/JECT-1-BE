package ject.mycode.domain.tourApi.dto;

import lombok.Data;
import java.util.List;

@Data
public class DetailCommonResponse {
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
	}

	@Data
	public static class Items {
		private List<DetailItem> item;
	}

	@Data
	public static class DetailItem {
		private String homepage;
		private String overview;
		private String program;
	}
}
