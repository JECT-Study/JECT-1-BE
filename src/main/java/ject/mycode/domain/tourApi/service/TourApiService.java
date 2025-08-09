package ject.mycode.domain.tourApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ject.mycode.domain.tourApi.dto.ContentSaveDto;
import ject.mycode.domain.tourApi.dto.DetailCommonResponse;
import ject.mycode.domain.tourApi.dto.TourApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.contentImage.entity.ContentImage;
import ject.mycode.domain.contentImage.repository.ContentImageRepository;
import ject.mycode.domain.region.entity.Region;
import ject.mycode.domain.region.repository.RegionRepository;
import ject.mycode.domain.user.enums.ContentStatus;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
	private final ObjectMapper objectMapper;

	private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	// 지역 코드 매핑 (Tour API areacode -> DB region_id)
	// Tour API 지역코드 참조: 1-서울, 2-인천, 3-대전, 4-대구, 5-광주, 6-부산, 7-울산, 8-세종, 31-경기, 32-강원, 33-충북, 34-충남, 35-경북, 36-경남, 37-전북, 38-전남, 39-제주
	private final Map<String, Long> areaCodeToRegionId = Map.ofEntries(
		Map.entry("1", 1L),   // 서울 → 서울
		Map.entry("2", 2L),   // 인천 → 경기·인천
		Map.entry("31", 2L),  // 경기 → 경기·인천
		Map.entry("32", 3L),  // 강원 → 강원
		Map.entry("33", 4L),  // 충북 → 충청권(충북·대전·세종)
		Map.entry("3", 4L),   // 대전 → 충청권(충북·대전·세종)
		Map.entry("8", 4L),   // 세종 → 충청권(충북·대전·세종)
		Map.entry("34", 5L),  // 충남 → 충남
		Map.entry("4", 6L),   // 대구 → 대구·경북
		Map.entry("35", 6L),  // 경북 → 대구·경북
		Map.entry("36", 7L),  // 경남 → 경남·울산
		Map.entry("7", 7L),   // 울산 → 경남·울산
		Map.entry("5", 8L),   // 광주 → 광주·전남
		Map.entry("38", 8L),  // 전남 → 광주·전남
		Map.entry("37", 9L),  // 전북 → 전북
		Map.entry("6", 10L),  // 부산 → 부산
		Map.entry("39", 11L)  // 제주 → 제주
	);

	public void fetchAndSaveFestivals(String startDate) {
		try {
			// 1. 축제 정보 조회
			TourApiResponse festivals = fetchFestivals(startDate);

			if (festivals == null || festivals.getResponse() == null ||
				festivals.getResponse().getBody() == null ||
				festivals.getResponse().getBody().getItems() == null) {
				log.warn("축제 데이터가 없습니다.");
				return;
			}

			List<TourApiResponse.FestivalItem> festivalItems = festivals.getResponse().getBody().getItems().getItem();

			for (TourApiResponse.FestivalItem item : festivalItems) {
				try {
					// 2. 상세 정보 조회 (detailCommon1)
					DetailCommonResponse detailInfo = fetchDetailCommon(item.getContentid());

					// 4. DTO 변환 및 저장
					ContentSaveDto contentDto = convertToContentDto(item, detailInfo);
					if (contentDto != null) {
						saveContent(contentDto);
					}

					// API 호출 제한을 위한 딜레이 (2번 호출하므로 조금 더 길게)
					Thread.sleep(200);

				} catch (Exception e) {
					log.error("축제 데이터 처리 중 오류 발생: contentId={}, error={}",
						item.getContentid(), e.getMessage());
				}
			}

		} catch (Exception e) {
			log.error("축제 데이터 수집 중 오류 발생", e);
			throw new RuntimeException("축제 데이터 수집 실패", e);
		}
	}

	private TourApiResponse fetchFestivals(String startDate) {
		try {
			// URL 문자열 직접 구성
			String url = String.format(
				"%s/searchFestival2?MobileOS=ETC&MobileApp=MyApp&_type=json&eventStartDate=%s&serviceKey=%s",
				BASE_URL, startDate, serviceKey
			);

			log.info("축제 API 호출 URL: {}", url);

			// URI 클래스를 사용하여 인코딩 처리
			URI uri = new URI(url);

			// HTTP 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json");
			headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
			headers.set("Content-Type", "application/json;charset=UTF-8");

			HttpEntity<String> entity = new HttpEntity<>(headers);

			// exchange 메서드에 URI 객체 사용
			ResponseEntity<String> responseEntity = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				entity,
				String.class
			);

			String response = responseEntity.getBody();

			// 디버깅 로그
			log.info("=== API 응답 디버깅 시작 ===");
			log.info("HTTP 상태 코드: {}", responseEntity.getStatusCode());
			log.info("응답 헤더: {}", responseEntity.getHeaders());
			log.info("응답 길이: {}", response != null ? response.length() : "null");

			if (response != null && !response.isEmpty()) {
				String preview = response.length() > 200 ? response.substring(0, 200) : response;
				log.info("응답 미리보기: {}", preview);

				char firstChar = response.charAt(0);
				log.info("첫 번째 문자: '{}' (ASCII: {})", firstChar, (int)firstChar);

				if (response.startsWith("{") || response.startsWith("[")) {
					log.info("응답 타입: JSON으로 확인됨");
				} else if (response.startsWith("<")) {
					log.error("응답 타입: XML/HTML - 전체 내용: {}", response);
					throw new RuntimeException("API 응답이 JSON이 아닌 XML/HTML입니다: " + response);
				}
			}
			log.info("=== API 응답 디버깅 종료 ===");

			return objectMapper.readValue(response, TourApiResponse.class);

		} catch (Exception e) {
			log.error("축제 API 호출 중 오류 발생", e);
			throw new RuntimeException("축제 API 호출 실패", e);
		}
	}

	private DetailCommonResponse fetchDetailCommon(String contentId) {
		try {
			// URL 문자열 직접 구성
			String url = String.format(
				"%s/detailCommon2?MobileOS=ETC&MobileApp=MyApp&_type=json&contentId=%s&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&serviceKey=%s",
				BASE_URL, contentId, serviceKey
			);

			log.debug("상세정보 API 호출 URL: {}", url);

			// URI 클래스를 사용하여 인코딩 처리
			URI uri = new URI(url);

			// 동일한 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json");
			headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
			headers.set("Content-Type", "application/json;charset=UTF-8");

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<String> responseEntity = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				entity,
				String.class
			);

			String response = responseEntity.getBody();

			// 상세정보도 XML 응답인지 확인
			if (response != null && response.startsWith("<")) {
				log.warn("상세정보 API도 XML 응답: contentId={}", contentId);
				return null;
			}

			return objectMapper.readValue(response, DetailCommonResponse.class);

		} catch (Exception e) {
			log.error("상세정보 API 호출 중 오류 발생: contentId={}", contentId, e);
			return null;
		}
	}

	private ContentSaveDto convertToContentDto(TourApiResponse.FestivalItem item,
		DetailCommonResponse detailInfo) {
		try {
			// 지역 매핑
			Long regionId = areaCodeToRegionId.get(item.getAreacode());
			if (regionId == null) {
				log.warn("매핑되지 않은 지역 코드: {}", item.getAreacode());
				return null;
			}

			// 날짜 변환
			LocalDate startDate = parseDate(item.getEventstartdate());
			LocalDate endDate = parseDate(item.getEventenddate());

			if (startDate == null || endDate == null) {
				log.warn("잘못된 날짜 형식: start={}, end={}",
					item.getEventstartdate(), item.getEventenddate());
				return null;
			}

			// 좌표 변환
			Double latitude = parseCoordinate(item.getMapy());
			Double longitude = parseCoordinate(item.getMapx());

			if (latitude == null || longitude == null) {
				log.warn("잘못된 좌표 형식: lat={}, lng={}", item.getMapy(), item.getMapx());
				return null;
			}

			// 상세 정보에서 추가 데이터 추출
			String overview = "";
			String homepage = "";
			String program = "";
			List<String> imageUrls = new ArrayList<>();

			if (detailInfo != null && detailInfo.getResponse() != null &&
				detailInfo.getResponse().getBody() != null &&
				detailInfo.getResponse().getBody().getItems() != null &&
				!detailInfo.getResponse().getBody().getItems().getItem().isEmpty()) {

				DetailCommonResponse.DetailItem detail =
					detailInfo.getResponse().getBody().getItems().getItem().get(0);

				overview = cleanHtml(detail.getOverview());
				homepage = detail.getHomepage();
				program = detail.getProgram();
			}

			// 이미지 URL 추가 (Festival API에서도)
			if (item.getFirstimage() != null && !item.getFirstimage().isEmpty()) {
				imageUrls.add(item.getFirstimage());
			}
			if (item.getFirstimage2() != null && !item.getFirstimage2().isEmpty()) {
				imageUrls.add(item.getFirstimage2());
			}

			// 중복 제거
			imageUrls = imageUrls.stream().distinct().toList();

			return ContentSaveDto.builder()
				.title(item.getTitle())
				.placeName(item.getTitle())
				.introduction(overview.length() > 100 ?
					overview.substring(0, 100) + "..." : overview)
				.description(program)
				.address(item.getAddr1() + (item.getAddr2() != null ? " " + item.getAddr2() : ""))
				.startDate(startDate)
				.endDate(endDate)
				.latitude(latitude)
				.longitude(longitude)
				.homepageUrl(homepage)
				.contentType(ContentType.FESTIVAL)
				.regionId(regionId)
				.imageUrls(imageUrls)
				.apiId(item.getContentid())
				.build();

		} catch (Exception e) {
			log.error("DTO 변환 중 오류 발생: contentId={}", item.getContentid(), e);
			return null;
		}
	}

	private LocalDate parseDate(String dateStr) {
		if (dateStr == null || dateStr.length() != 8) {
			return null;
		}
		try {
			return LocalDate.parse(dateStr, DATE_FORMATTER);
		} catch (Exception e) {
			return null;
		}
	}

	private Double parseCoordinate(String coord) {
		if (coord == null || coord.isEmpty()) {
			return null;
		}
		try {
			return Double.parseDouble(coord);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String cleanHtml(String html) {
		if (html == null) return "";
		return html.replaceAll("<[^>]*>", "").trim();
	}

	private void saveContent(ContentSaveDto dto) {
		try {
			// 지역 조회
			Region region = regionRepository.findById(dto.getRegionId())
				.orElseThrow(() -> new RuntimeException("지역을 찾을 수 없습니다: " + dto.getRegionId()));

			// Content 엔티티 생성 (id, createdAt, updatedAt은 BaseEntity에서 자동 처리)
			Content content = Content.builder()
				.title(dto.getTitle())
				.placeName(dto.getPlaceName())
				.introduction(dto.getIntroduction())
				.description(dto.getDescription())
				.address(dto.getAddress())
				.startDate(dto.getStartDate())
				.endDate(dto.getEndDate())
				.isAlwaysOpen(false)
				.openingHour(LocalTime.of(9, 0))
				.closedHour(LocalTime.of(18, 0))
				.latitude(dto.getLatitude())
				.longitude(dto.getLongitude())
				.homepageUrl(dto.getHomepageUrl())
				.status(ContentStatus.ACTIVE)
				.contentType(dto.getContentType())
				.region(region)
				.apiId(dto.getApiId())
				.build();

			Content savedContent = contentRepository.save(content);

			// 이미지 저장 (id, createdAt, updatedAt은 BaseEntity에서 자동 처리)
			if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
				for (String imageUrl : dto.getImageUrls()) {
					ContentImage contentImage = ContentImage.builder()
						.imageUrl(imageUrl)
						.content(savedContent)
						.build();
					contentImageRepository.save(contentImage);
				}
			}

			log.info("콘텐츠 저장 완료: {}", savedContent.getTitle());

		} catch (Exception e) {
			log.error("콘텐츠 저장 중 오류 발생: title={}", dto.getTitle(), e);
		}
	}
}
