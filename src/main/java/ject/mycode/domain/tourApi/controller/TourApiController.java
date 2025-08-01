package ject.mycode.domain.tourApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ject.mycode.domain.tourApi.service.TourApiService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/tour")
@RequiredArgsConstructor
@Slf4j
public class TourApiController {

	private final TourApiService tourApiService;

	@GetMapping("/festivals")
	public ResponseEntity<String> fetchFestivals(
		@RequestParam(required = false) String startDate) {

		try {
			// 기본값: 오늘 날짜
			if (startDate == null || startDate.isEmpty()) {
				startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			}

			log.info("축제 데이터 수집 시작: startDate={}", startDate);
			tourApiService.fetchAndSaveFestivals(startDate);

			return ResponseEntity.ok("축제 데이터 수집이 완료되었습니다.");

		} catch (Exception e) {
			log.error("축제 데이터 수집 중 오류 발생", e);
			return ResponseEntity.internalServerError()
				.body("축제 데이터 수집 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

	@GetMapping("/festivals/batch")
	public ResponseEntity<String> fetchFestivalsBatch(
		@RequestParam String startDate,
		@RequestParam String endDate) {

		try {
			log.info("배치 축제 데이터 수집 시작: {} ~ {}", startDate, endDate);

			LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
			LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

			LocalDate current = start;
			while (!current.isAfter(end)) {
				String dateStr = current.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
				tourApiService.fetchAndSaveFestivals(dateStr);
				current = current.plusMonths(1); // 월 단위로 수집

				// 과도한 API 호출 방지
				Thread.sleep(1000);
			}

			return ResponseEntity.ok("배치 축제 데이터 수집이 완료되었습니다.");

		} catch (Exception e) {
			log.error("배치 축제 데이터 수집 중 오류 발생", e);
			return ResponseEntity.internalServerError()
				.body("배치 축제 데이터 수집 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}
