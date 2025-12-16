package ject.mycode.domain.tourApi.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.contentImage.entity.ContentImage;
import ject.mycode.domain.contentImage.repository.ContentImageRepository;
import ject.mycode.domain.region.entity.Region;
import ject.mycode.domain.region.repository.RegionRepository;
import ject.mycode.domain.tourApi.dto.*;
import ject.mycode.domain.user.enums.ContentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourApiService {

	@Value("${tour.api.key}")
	private String serviceKey;

	private final RestTemplate restTemplate;
	private final ContentRepository contentRepository;
	private final ContentImageRepository contentImageRepository;
	private final RegionRepository regionRepository;

	private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

	// areacode → region_id (프로젝트에 맞게 조정)
	private final Map<String, Long> areaCodeToRegionId = Map.ofEntries(
		Map.entry("1", 1L), Map.entry("2", 2L), Map.entry("31", 2L),
		Map.entry("32", 3L), Map.entry("33", 4L), Map.entry("3", 4L),
		Map.entry("8", 4L), Map.entry("34", 5L), Map.entry("4", 6L),
		Map.entry("35", 6L), Map.entry("36", 7L), Map.entry("7", 7L),
		Map.entry("5", 8L), Map.entry("38", 8L), Map.entry("37", 9L),
		Map.entry("6", 10L), Map.entry("39", 11L)
	);

	/* ===================== Public API ===================== */

	/** @param startDate yyyyMMdd (예: 20250101) */
	public void fetchAndSaveFestivals(String startDate) {
		log.info("=== [TourAPI] 축제 수집 시작 | 시작일자={} ===", startDate);

		int totalProcessed = 0, totalSaved = 0, totalSkipped = 0, totalErrors = 0;
		int totalOverviewOk = 0, totalHomepageOk = 0, totalHoursOk = 0, totalImagesSaved = 0, totalInfoLines = 0, totalPlaceOk = 0;
		int totalIntroFromList = 0, totalIntroFromOverview = 0;

		long t0 = System.currentTimeMillis();

		SearchFestivalResponse first = searchFestival(startDate, 1);
		if (!validSearch(first)) {
			log.warn("[TourAPI] 검색 결과 없음 또는 실패");
			return;
		}

		int totalCount = first.getResponse().getBody().getTotalCount();
		int rows = first.getResponse().getBody().getNumOfRows();
		int totalPages = (int) Math.ceil((double) totalCount / rows);

		log.info("[TourAPI] 총 건수={} | 페이지당={} | 총 페이지={}", totalCount, rows, totalPages);

		for (int page = 1; page <= totalPages; page++) {
			long pageStart = System.currentTimeMillis();

			SearchFestivalResponse pageResp = (page == 1) ? first : searchFestival(startDate, page);
			if (!validSearch(pageResp)) {
				log.warn("[TourAPI] 페이지 {} 응답 없음, 중단", page);
				break;
			}

			List<SearchFestivalResponse.Item> items = pageResp.getResponse().getBody().getItems().getItem();
			int pageCnt = items.size();
			log.info("---- 페이지 {}/{} 처리 시작 | 아이템수={} ----", page, totalPages, pageCnt);

			int pageProcessed = 0, pageSaved = 0, pageSkipped = 0, pageErrors = 0;
			int pageOverviewOk = 0, pageHomepageOk = 0, pageHoursOk = 0, pageImagesSaved = 0, pageInfoLines = 0, pagePlaceOk = 0;
			int pageIntroFromList = 0, pageIntroFromOverview = 0;

			for (SearchFestivalResponse.Item it : items) {
				pageProcessed++; totalProcessed++;
				String cid = it.getContentid();

				try {
					if (cid == null || contentRepository.existsByApiId(cid)) {
						pageSkipped++; totalSkipped++;
						continue;
					}

					String ctype = nz(it.getContenttypeid(), "15");
					DetailCommonResponse common = detailCommon(cid);
					DetailInfoResponse info   = detailInfo(cid, ctype);
					DetailIntroResponse intro = detailIntro(cid, ctype); // placeName + playtime

					ExtractStats stats = new ExtractStats();

					Content entity = mapToEntity(it, common, info, intro, stats);
					if (entity == null) {
						pageErrors++; totalErrors++;
						continue;
					}

                    Content saved = contentRepository.save(entity);

                    // 이미지 저장: firstimage/firstimage2
                    List<String> images = collectFirstTwoImages(it, common);
                    for (String url : images) {
                        if (url == null || url.isBlank()) continue;
                        contentImageRepository.save(
                                ContentImage.builder().imageUrl(url.trim()).content(saved).build()
                        );
                        pageImagesSaved++; totalImagesSaved++;
                    }

                    if (stats.overviewOk) { pageOverviewOk++; totalOverviewOk++; }
                    if (stats.homepageOk) { pageHomepageOk++; totalHomepageOk++; }
					if (stats.hoursOk)    { pageHoursOk++;    totalHoursOk++; }
					if (stats.placeOk)    { pagePlaceOk++;    totalPlaceOk++; }
					if (stats.introFromList) { pageIntroFromList++; totalIntroFromList++; }
					if (stats.introFromOverview) { pageIntroFromOverview++; totalIntroFromOverview++; }
					pageInfoLines += stats.infoLines; totalInfoLines += stats.infoLines;

					pageSaved++; totalSaved++;

					if (totalProcessed % 100 == 0 || totalProcessed == totalCount) {
						double pct = (totalCount == 0) ? 100.0 : (totalProcessed * 100.0 / totalCount);
						log.info("[진행] {}/{} ({:.2f}%) 완료", totalProcessed, totalCount, pct);
					}

				} catch (Exception e) {
					pageErrors++; totalErrors++;
					log.error("아이템 처리 실패 | contentId={} | title={} | err={}",
						cid, nz(it.getTitle(), "-"), e.getMessage(), e);
				}
			}

			long pageMs = System.currentTimeMillis() - pageStart;
			log.info("---- 페이지 {}/{} 완료 | 처리:{} 저장:{} 건너뜀:{} 오류:{} | overview_OK:{} homepage_OK:{} hours_OK:{} place_OK:{} intro_from_list:{} intro_from_overview:{} img_saved:{} infoLines:{} | 경과:{}ms ----",
				page, totalPages, pageProcessed, pageSaved, pageSkipped, pageErrors,
				pageOverviewOk, pageHomepageOk, pageHoursOk, pagePlaceOk, pageIntroFromList, pageIntroFromOverview, pageImagesSaved, pageInfoLines, pageMs);
		}

		long took = System.currentTimeMillis() - t0;
		log.info("=== [TourAPI] 축제 수집 완료 ===");
		log.info("총건수:{} | 총처리:{} | 저장:{} | 중복건너뜀:{} | 오류:{} | overview_OK:{} | homepage_OK:{} | hours_OK:{} | place_OK:{} | intro_from_list:{} | intro_from_overview:{} | 이미지저장:{} | infotext라인:{} | 총소요:{}ms",
			totalCount, totalProcessed, totalSaved, totalSkipped, totalErrors,
			totalOverviewOk, totalHomepageOk, totalHoursOk, totalPlaceOk, totalIntroFromList, totalIntroFromOverview, totalImagesSaved, totalInfoLines, took);
	}

	/* ===================== HTTP Calls (JSON only) ===================== */

	private SearchFestivalResponse searchFestival(String startDate, int pageNo) {
		String url = String.format(
			"%s/searchFestival2?MobileOS=ETC&MobileApp=MyApp&_type=json&eventStartDate=%s&numOfRows=100&pageNo=%d&serviceKey=%s",
			BASE_URL, startDate, pageNo, serviceKey
		);
		return exchange(url, SearchFestivalResponse.class);
	}

	private DetailCommonResponse detailCommon(String contentId) {
		String url = String.format(
			"%s/detailCommon2?serviceKey=%s&MobileApp=MyApp&MobileOS=ETC&pageNo=1&numOfRows=10&contentId=%s&_type=json",
			BASE_URL, serviceKey, contentId
		);

		return exchange(url, DetailCommonResponse.class);
	}

	private DetailInfoResponse detailInfo(String contentId, String contentTypeId) {
		String url = String.format(
			"%s/detailInfo2?MobileOS=ETC&MobileApp=MyApp&_type=json&contentId=%s&contentTypeId=%s&serviceKey=%s",
			BASE_URL, contentId, contentTypeId, serviceKey
		);
		return exchange(url, DetailInfoResponse.class);
	}

	private DetailIntroResponse detailIntro(String contentId, String contentTypeId) {
		String url = String.format(
			"%s/detailIntro2?MobileOS=ETC&MobileApp=MyApp&_type=json&contentId=%s&contentTypeId=%s&serviceKey=%s",
			BASE_URL, contentId, contentTypeId, serviceKey
		);
		return exchange(url, DetailIntroResponse.class);
	}

    private <T> T exchange(String url, Class<T> type) {
        try {
            ResponseEntity<String> res = restTemplate.exchange(
                    new URI(url), HttpMethod.GET, new HttpEntity<>(jsonHeaders()), String.class
            );
            String body = res.getBody();

            // ✅ 공통 로그 찍기
            log.info("[TourAPI][exchange] url={} | raw={}", url, body);

            if (body == null || body.isBlank()) return null;
            return mapper().readValue(body, type);
        } catch (Exception e) {
            log.error("HTTP 실패: {}", url, e);
            return null;
        }
    }


    /* ===================== Mapping ===================== */

	private Content mapToEntity(SearchFestivalResponse.Item item,
		DetailCommonResponse common,
		DetailInfoResponse info,
		DetailIntroResponse intro,
		ExtractStats stats) {

		// region
		Long regionId = areaCodeToRegionId.get(item.getAreacode());
		if (regionId == null) {
			log.warn("미매핑 지역코드: {}", item.getAreacode());
			return null;
		}
		Region region = regionRepository.findById(regionId)
			.orElseThrow(() -> new IllegalStateException("Region not found: " + regionId));

		// dates
		LocalDate start = parseDate(item.getEventstartdate());
		LocalDate end   = parseDate(item.getEventenddate());
		if (start == null || end == null) {
			log.warn("잘못된 날짜: start={}, end={}", item.getEventstartdate(), item.getEventenddate());
			return null;
		}

		// coords
		Double lat = parseDouble(item.getMapy());
		Double lng = parseDouble(item.getMapx());
		if (lat == null || lng == null) {
			log.warn("잘못된 좌표: lat={}, lng={}", item.getMapy(), item.getMapx());
			return null;
		}

		// overview (clean)
		String overview = firstOverview(common);
		if (overview != null && !overview.isBlank()) stats.overviewOk = true;

		// info 텍스트(모든 infotext 연결, 개행 보존)
		String infoText = concatInfoTexts(info, stats);

		// ================= 핵심 분배 로직 =================
		String baseText = notBlank(infoText) ? infoText : overview;

		String introduction;
		String description;

		if (notBlank(baseText)) {
			SplitParts sp = splitNarrativeAndList(baseText);
			if (sp.hasList()) {
				description  = sp.narrative().isBlank() ? defaultDescription(item, overview) : sp.narrative().trim();
				introduction = sp.list().trim();
				stats.introFromList = true;
			} else {
				description  = baseText.trim();
				introduction = notBlank(overview) ? overview : limitIntro(baseText);
				stats.introFromOverview = true;
			}
		} else {
			description  = defaultDescription(item, overview);
			introduction = notBlank(overview) ? overview : limitIntro(description);
			stats.introFromOverview = true;
		}
		// ==================================================

		// 홈페이지: HTML에서 URL 추출
		String homepageUrl = extractHomepageUrl(common);
		if (notBlank(homepageUrl)) stats.homepageOk = true;

		String telNumber = firstTel(common);

		// address
		String addr = Optional.ofNullable(item.getAddr1()).orElse("");
		if (item.getAddr2() != null && !item.getAddr2().isBlank()) addr += " " + item.getAddr2();
		if (addr.isBlank()) addr = "주소 미상";

		// 운영시간: detailIntro2.playtime만 사용
		String playtimeRaw = firstPlaytime(intro);
		OperatingHours hours = hoursFromPlaytime(playtimeRaw);
		if (Boolean.TRUE.equals(hours.isAlwaysOpen) || (hours.open != null && hours.close != null)) {
			stats.hoursOk = true;
		}

		// placeName = detailIntro2.eventplace
		String placeName = firstEventPlace(intro);
		if (!notBlank(placeName)) placeName = nz(item.getTitle(), "장소 미상");
		else stats.placeOk = true;

		String contentTypeId = nz(item.getContenttypeid(), "15");
		String cat1 = nz(item.getCat1(), "");
		String cat2 = nz(item.getCat2(), "");
		String cat3 = nz(item.getCat3(), "");
		ContentType resolvedType = resolveCategoryByTourApi(contentTypeId, cat1, cat2, cat3);

		return Content.builder()
			.apiId(item.getContentid())
			.title(nz(item.getTitle(), "제목 미상"))
			.placeName(placeName)
			.introduction(introduction)
			.description(description)
			.address(addr)
			.startDate(start)
			.endDate(end)
			.isAlwaysOpen(hours.isAlwaysOpen)
			.openingHour(hours.open)
			.closedHour(hours.close)
			.latitude(lat)
			.longitude(lng)
			.homepageUrl(homepageUrl)
			.telNumber(telNumber)
			.status(ContentStatus.ACTIVE)
			.views(0)
			.contentType(resolvedType)
			.region(region)
			.build();
	}

	private List<String> collectFirstTwoImages(SearchFestivalResponse.Item listItem, DetailCommonResponse common) {
		List<String> urls = new ArrayList<>();
		if (notBlank(listItem.getFirstimage()))  urls.add(listItem.getFirstimage());
		if (notBlank(listItem.getFirstimage2())) urls.add(listItem.getFirstimage2());

		DetailCommonResponse.DetailItem d = firstDetailCommonItem(common);
		if (urls.isEmpty() && d != null) {
			if (notBlank(d.getFirstimage()))  urls.add(d.getFirstimage());
			if (notBlank(d.getFirstimage2())) urls.add(d.getFirstimage2());
		}
		return urls.stream().filter(Objects::nonNull).map(String::trim).distinct().toList();
	}

	/* ===================== DTO helpers ===================== */

	private boolean validSearch(SearchFestivalResponse r) {
		return r != null && r.getResponse() != null &&
			r.getResponse().getBody() != null &&
			r.getResponse().getBody().getItems() != null &&
			r.getResponse().getBody().getItems().getItem() != null &&
			!r.getResponse().getBody().getItems().getItem().isEmpty();
	}

	private DetailCommonResponse.DetailItem firstDetailCommonItem(DetailCommonResponse r) {
		if (r == null || r.getResponse() == null || r.getResponse().getBody() == null ||
			r.getResponse().getBody().getItems() == null || r.getResponse().getBody().getItems().getItem() == null ||
			r.getResponse().getBody().getItems().getItem().isEmpty()) return null;
		return r.getResponse().getBody().getItems().getItem().get(0);
	}

	private String firstOverview(DetailCommonResponse r) {
		DetailCommonResponse.DetailItem d = firstDetailCommonItem(r);
		return cleanHtml(d == null ? null : d.getOverview());
	}

	// 홈페이지 URL 추출 로직 개선
	private String extractHomepageUrl(DetailCommonResponse r) {
		DetailCommonResponse.DetailItem d = firstDetailCommonItem(r);
		if (d == null || !notBlank(d.getHomepage())) return null;

		String homepageField = d.getHomepage();

		// HTML 태그에서 href 속성의 URL 추출
		Pattern hrefPattern = Pattern.compile("href=[\"'](https?://[^\"']+)[\"']");
		Matcher hrefMatcher = hrefPattern.matcher(homepageField);
		if (hrefMatcher.find()) {
			return hrefMatcher.group(1);
		}

		// 단순 URL인 경우 (http로 시작)
		if (homepageField.startsWith("http")) {
			return homepageField.trim();
		}

		// HTML 태그 제거 후 URL 찾기
		String cleaned = cleanHtml(homepageField);
		Pattern urlPattern = Pattern.compile("(https?://[^\\s]+)");
		Matcher urlMatcher = urlPattern.matcher(cleaned);
		if (urlMatcher.find()) {
			return urlMatcher.group(1);
		}

		return null;
	}

	private String concatInfoTexts(DetailInfoResponse r, ExtractStats stats) {
		if (r == null || r.getResponse() == null || r.getResponse().getBody() == null ||
			r.getResponse().getBody().getItems() == null || r.getResponse().getBody().getItems().getItem() == null ||
			r.getResponse().getBody().getItems().getItem().isEmpty()) return null;

		StringBuilder sb = new StringBuilder();
		for (DetailInfoResponse.InfoItem it : r.getResponse().getBody().getItems().getItem()) {
			if (notBlank(it.getInfotext())) {
				if (!sb.isEmpty()) sb.append("\n");
				String cleaned = cleanHtml(it.getInfotext());
				sb.append(cleaned);
				stats.infoLines += Math.max(1, cleaned.split("\\R", -1).length);
			}
		}
		String s = sb.toString().trim();
		return s.isBlank() ? null : s;
	}

	/** detailIntro2에서 eventplace 읽기 */
	private String firstEventPlace(DetailIntroResponse r) {
		if (r == null || r.getResponse() == null || r.getResponse().getBody() == null ||
			r.getResponse().getBody().getItems() == null || r.getResponse().getBody().getItems().getItem() == null ||
			r.getResponse().getBody().getItems().getItem().isEmpty()) return null;
		DetailIntroResponse.IntroItem it = r.getResponse().getBody().getItems().getItem().get(0);
		String place = it.getEventplace();
		return (place == null || place.isBlank()) ? null : place.trim();
	}

	/** detailIntro2에서 playtime 읽기 */
	private String firstPlaytime(DetailIntroResponse r) {
		if (r == null || r.getResponse() == null || r.getResponse().getBody() == null ||
			r.getResponse().getBody().getItems() == null || r.getResponse().getBody().getItems().getItem() == null ||
			r.getResponse().getBody().getItems().getItem().isEmpty()) return null;
		DetailIntroResponse.IntroItem it = r.getResponse().getBody().getItems().getItem().get(0);
		String pt = it.getPlaytime();
		return (pt == null || pt.isBlank()) ? null : pt.trim();
	}

	/* ===================== 번호목록 분리 로직 ===================== */

	/** base 텍스트에서 최초 번호목록(예: "1. " 또는 "1) ") 시작 지점 기준으로 서술/목록 분리 */
	private SplitParts splitNarrativeAndList(String text) {
		if (!notBlank(text)) return new SplitParts("", "", false);
		Pattern p = Pattern.compile("(?m)^\\s*\\d{1,2}[\\.)]\\s+");
		Matcher m = p.matcher(text);
		if (m.find()) {
			int idx = m.start();
			String narrative = text.substring(0, idx).trim();
			String listPart  = text.substring(idx).trim();
			return new SplitParts(narrative, listPart, true);
		}
		return new SplitParts(text.trim(), "", false);
	}

	/* ===================== Time parsing (playtime 전용) ===================== */

	private OperatingHours hoursFromPlaytime(String playtime) {
		if (!notBlank(playtime)) return new OperatingHours(null, null, null);

		String norm = playtime.trim().replaceAll("\\s+", " ");

		// 24시간/상시 키워드
		if (norm.contains("24시간") || norm.contains("상시") || norm.contains("항시")) {
			return new OperatingHours(true, null, null);
		}

		// 애매표현은 시간 저장하지 않음
		if (Pattern.compile("(이후|이전|부터|까지|이상|이하|연중)").matcher(norm).find()) {
			return new OperatingHours(null, null, null);
		}

		// 구분자 기준으로 구간 분리
		String[] parts = norm.split("\\s*[-~–]\\s*");
		if (parts.length < 2) {
			String[] alt = norm.split("[,/]|\\s{2,}");
			if (alt.length >= 2) parts = new String[] { alt[0].trim(), alt[1].trim() };
		}
		if (parts.length < 2) return new OperatingHours(null, null, null);

		LocalTime open  = extractFirstTime(parts[0]);
		LocalTime close = extractFirstTime(parts[1]);
		if (open == null || close == null) return new OperatingHours(null, null, null);

		return new OperatingHours(false, open, close);
	}

	/** 부분 문자열에서 첫 번째 시각을 파싱 */
	private LocalTime extractFirstTime(String s) {
		if (s == null) return null;
		String seg = s.trim();

		boolean hasPM = Pattern.compile("(?i)오후|PM").matcher(seg).find();
		boolean hasAM = Pattern.compile("(?i)오전|AM").matcher(seg).find();

		// 1) hh:mm
		Matcher m1 = Pattern.compile("(\\d{1,2}):(\\d{2})").matcher(seg);
		if (m1.find()) {
			int h = safeInt(m1.group(1), -1);
			int mi = safeInt(m1.group(2), -1);
			if (h >= 0 && mi >= 0) {
				h = adjustHourByAmPm(h, hasAM, hasPM);
				return toLocalTimeSafe(h, mi);
			}
		}

		// 2) h시 mm분 / h시
		Matcher m2 = Pattern.compile("(\\d{1,2})\\s*시(?:\\s*(\\d{1,2})\\s*분)?").matcher(seg);
		if (m2.find()) {
			int h  = safeInt(m2.group(1), -1);
			int mi = (m2.group(2) != null) ? safeInt(m2.group(2), 0) : 0;
			h = adjustHourByAmPm(h, hasAM, hasPM);
			return toLocalTimeSafe(h, mi);
		}

		// 3) hhmm 또는 hmm (예: 0900, 900)
		Matcher m3 = Pattern.compile("\\b(\\d{3,4})\\b").matcher(seg);
		if (m3.find()) {
			String d = m3.group(1);
			int h, mi;
			if (d.length() == 3) { h = safeInt(d.substring(0,1), -1); mi = safeInt(d.substring(1), -1); }
			else { h = safeInt(d.substring(0,2), -1); mi = safeInt(d.substring(2), -1); }
			h = adjustHourByAmPm(h, hasAM, hasPM);
			return toLocalTimeSafe(h, mi);
		}

		// 4) 숫자만 (시간)
		Matcher m4 = Pattern.compile("\\b(\\d{1,2})\\b").matcher(seg);
		if (m4.find()) {
			int h = safeInt(m4.group(1), -1);
			h = adjustHourByAmPm(h, hasAM, hasPM);
			return toLocalTimeSafe(h, 0);
		}

		return null;
	}

	private int adjustHourByAmPm(int hour, boolean hasAM, boolean hasPM) {
		if (hour < 0) return hour;
		if (hasPM && hour < 12) hour += 12;
		if (hasAM && hour == 12) hour = 0;
		return hour;
	}

	private LocalTime toLocalTimeSafe(int h, int mi) {
		try {
			if (h < 0 || h > 23 || mi < 0 || mi > 59) return null;
			return LocalTime.of(h, mi);
		} catch (Exception e) { return null; }
	}

	private int safeInt(String s, int def) { try { return Integer.parseInt(s); } catch (Exception e) { return def; } }

	/* ===================== Common Utils ===================== */

	private LocalDate parseDate(String s) {
		if (s == null || s.length() != 8) return null;
		try { return LocalDate.parse(s, DATE_FMT); } catch (Exception ignored) { return null; }
	}

	private Double parseDouble(String s) {
		if (!notBlank(s)) return null;
		try { return Double.parseDouble(s.trim()); } catch (Exception ignored) { return null; }
	}

	private String cleanHtml(String html) {
		if (!notBlank(html)) return "";
		return html
			.replaceAll("(?i)<br\\s*/?>", "\n")
			.replaceAll("<[^>]*>", "")
			.replace("&lt;", "<").replace("&gt;", ">")
			.replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&")
			.replaceAll("[ \\t\\x0B\\f\\r]+", " ")
			.replaceAll("\\n\\s*\\n+", "\n")
			.trim();
	}

	private String limitIntro(String s) {
		if (s == null) return "";
		if (s.length() <= 160) return s;
		int dot = s.indexOf('.', 120);
		return (dot != -1 && dot <= 200) ? s.substring(0, dot + 1) : (s.substring(0, 160) + "...");
	}

	private boolean notBlank(String s) { return s != null && !s.isBlank(); }
	private String nz(String s, String d) { return notBlank(s) ? s : d; }

	private String defaultDescription(SearchFestivalResponse.Item item, String fallbackOverview) {
		if (notBlank(fallbackOverview)) return fallbackOverview;
		String t = nz(item.getTitle(), "축제 소개");
		return t + " 관련 안내입니다.";
	}

	private HttpHeaders jsonHeaders() {
		HttpHeaders h = new HttpHeaders();
		h.set("Accept", "application/json");
		h.set("User-Agent", "Mozilla/5.0");
		return h;
	}

	private ObjectMapper mapper() {
		ObjectMapper m = new ObjectMapper();
		m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		m.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		m.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		return m;
	}

	// 분류 로직 헬퍼
	private ContentType resolveCategoryByTourApi(String contentTypeId, String cat1, String cat2, String cat3) {
		if ("A0207".equalsIgnoreCase(cat2)) {
			return ContentType.FESTIVAL;
		}

		CategoryNames names = fetchCategoryNames(contentTypeId, cat1, cat2, cat3);

		String c2Name = names.cat2Name == null ? "" : names.cat2Name;
		String c3Name = names.cat3Name == null ? "" : names.cat3Name;

		if (c2Name.contains("축제")) return ContentType.FESTIVAL;

		if (containsAny(c3Name, "전시회", "박람회")) return ContentType.EXHIBITION;
		if (containsAny(c3Name, "전통공연", "연극", "뮤지컬", "오페라", "무용", "클래식", "대중콘서트", "영화", "넌버벌"))
			return ContentType.PERFORMANCE;
		if (containsAny(c3Name, "스포츠", "경기", "기타행사", "행사"))
			return ContentType.EVENT;

		return ContentType.EVENT;
	}

	private boolean containsAny(String src, String... keys) {
		if (src == null || src.isBlank()) return false;
		for (String k : keys) if (src.contains(k)) return true;
		return false;
	}

	// categoryCode2 호출: DTO 없이 JsonNode로 최소 파싱
	private CategoryNames fetchCategoryNames(String contentTypeId, String cat1, String cat2, String cat3) {
		try {
			String url2 = String.format(
				"%s/categoryCode2?MobileOS=ETC&MobileApp=MyApp&_type=json&contentTypeId=%s&cat1=%s&serviceKey=%s",
				BASE_URL, enc(contentTypeId), enc(cat1), serviceKey
			);
			JsonNode list2 = getJson(url2);
			String cat2Name = findNameByCode(list2, cat2);

			String url3 = String.format(
				"%s/categoryCode2?MobileOS=ETC&MobileApp=MyApp&_type=json&contentTypeId=%s&cat1=%s&cat2=%s&serviceKey=%s",
				BASE_URL, enc(contentTypeId), enc(cat1), enc(cat2), serviceKey
			);
			JsonNode list3 = getJson(url3);
			String cat3Name = findNameByCode(list3, cat3);

			return new CategoryNames(cat2Name, cat3Name);
		} catch (Exception e) {
			log.warn("[TourAPI] categoryCode 조회 실패: cat1={} cat2={} cat3={} err={}", cat1, cat2, cat3, e.toString());
			return new CategoryNames(null, null);
		}
	}

	// 공공데이터 응답 공통 파서(코드/명칭만 필요)
	private String findNameByCode(JsonNode root, String code) {
		if (root == null || code == null || code.isBlank()) return null;
		try {
			JsonNode items = root.path("response").path("body").path("items").path("item");
			if (items.isMissingNode()) return null;
			if (items.isArray()) {
				for (JsonNode it : items) {
					if (code.equalsIgnoreCase(it.path("code").asText(null))) {
						return it.path("name").asText(null);
					}
				}
			} else {
				if (code.equalsIgnoreCase(items.path("code").asText(null))) {
					return items.path("name").asText(null);
				}
			}
		} catch (Exception ignored) {}
		return null;
	}

	private String firstTel(DetailCommonResponse r) {
		DetailCommonResponse.DetailItem d = firstDetailCommonItem(r);
		if (d == null) return null;
		String tel = d.getTel();
		if (tel != null && !tel.isBlank()) return tel.trim();

		return null;
	}


	// 간단한 JSON 호출 유틸
	private JsonNode getJson(String url) {
		try {
			ResponseEntity<String> res = restTemplate.exchange(new URI(url), HttpMethod.GET, new HttpEntity<>(jsonHeaders()), String.class);
			String body = res.getBody();
			if (body == null || body.isBlank()) return null;
			return mapper().readTree(body);
		} catch (Exception e) {
			log.error("HTTP 실패(JSON): {}", url, e);
			return null;
		}
	}

	private String enc(String s) { return (s == null) ? "" : s; }

	private record CategoryNames(String cat2Name, String cat3Name) {}

	/* ======= Records & Stats ======= */

	private record OperatingHours(Boolean isAlwaysOpen, LocalTime open, LocalTime close) {}

	private record SplitParts(String narrative, String list, boolean hasList) {}

	private static class ExtractStats {
		boolean overviewOk = false;
		boolean homepageOk = false;
		boolean hoursOk = false;
		boolean placeOk = false;
		boolean introFromList = false;
		boolean introFromOverview = false;
		int infoLines = 0;
	}

    private List<String> collectTwoDistinctImages(SearchFestivalResponse.Item listItem, DetailCommonResponse common) {

        List<String> urls = new ArrayList<>();

        // ✅ firstimage MAN만 저장 (firstimage2 무시)
        if (notBlank(listItem.getFirstimage())) {
            urls.add(listItem.getFirstimage());
        }

        // detailCommon에서 firstimage만 (firstimage2 무시)
        DetailCommonResponse.DetailItem d = firstDetailCommonItem(common);
        if (urls.isEmpty() && d != null && notBlank(d.getFirstimage())) {
            urls.add(d.getFirstimage());
        }

        return urls.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .limit(1)  // ✅ 최대 1장만!
                .toList();
    }

}
