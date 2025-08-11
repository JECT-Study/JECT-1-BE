package ject.mycode.domain.content.repository.custom;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import ject.mycode.domain.content.dto.*;
import ject.mycode.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ject.mycode.domain.content.entity.QContent;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.contentImage.entity.QContentImage;
import ject.mycode.domain.favorite.entity.QFavorite;
import ject.mycode.domain.schedule.entity.QSchedule;
import ject.mycode.domain.tag.entity.QContentTag;
import ject.mycode.domain.tag.entity.QTag;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.enums.ContentStatus;
import lombok.RequiredArgsConstructor;

import static ject.mycode.domain.region.entity.QRegion.region;

@Repository
@RequiredArgsConstructor
public class ContentQueryRepositoryImpl implements ContentQueryRepository {

	private final JPAQueryFactory qf;
	private final QContent content = QContent.content;
	private final QContentImage contentImage = QContentImage.contentImage;
	QContentImage contentImageSub = new QContentImage("contentImageSub");
	private final QContentTag contentTag = QContentTag.contentTag;
	private final QTag tag = QTag.tag;
	private final QFavorite favorite = QFavorite.favorite;
	private final QSchedule schedule = QSchedule.schedule;

	@Override
	public ContentDetailsRes findDetailsByContentId(User user, Long contentId) {
		// TODO: 후에 날쿼리로 리팩토링
		// 비로그인 사용자인 경우
		if (user == null) {
			return qf.select(Projections.constructor(ContentDetailsRes.class,
					content.id,
					Expressions.nullExpression(), // likeId
					Expressions.nullExpression(), // scheduleId
					content.title,
					Expressions.constant(Collections.emptyList()),
					Expressions.constant(Collections.emptyList()),
					content.placeName,
					content.startDate,
					content.endDate,
					JPAExpressions.select(favorite.count())
						.from(favorite)
						.where(favorite.content.id.eq(contentId)),
					content.isAlwaysOpen,
					content.openingHour,
					content.closedHour,
					content.address,
					content.introduction,
					content.description,
					content.longitude,
					content.latitude
				))
				.from(content)
				.where(content.id.eq(contentId))
				.fetchOne();
		}

		// 로그인 상태인 경우
		QFavorite favoriteSub = QFavorite.favorite;

		return qf.select(Projections.constructor(ContentDetailsRes.class,
				content.id,
				JPAExpressions.select(favoriteSub.id)
					.from(favoriteSub)
					.where(favoriteSub.content.id.eq(contentId)
						.and(favoriteSub.user.id.eq(user.getId()))),
				JPAExpressions.select(schedule.id)
					.from(schedule)
					.where(schedule.content.id.eq(contentId)
						.and(schedule.user.id.eq(user.getId()))),
				content.title,
				Expressions.constant(Collections.emptyList()),
				Expressions.constant(Collections.emptyList()),
				content.placeName,
				content.startDate,
				content.endDate,
				JPAExpressions.select(favorite.count())
					.from(favorite)
					.where(favorite.content.id.eq(contentId)),
				content.isAlwaysOpen,
				content.openingHour,
				content.closedHour,
				content.address,
				content.introduction,
				content.description,
				content.longitude,
				content.latitude
			))
			.from(content)
			.where(content.id.eq(contentId))
			.fetchOne();
	}

	@Override
	public Page<FavoritesRes> findFavoritesByUserId(Long userId, ContentType contentType, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(favorite.user.id.eq(userId));

		if (contentType != null) {
			builder.and(content.contentType.eq(contentType));
		}

		QContentImage contentImageSub = new QContentImage("contentImageSub");

		List<FavoritesRes> contents = qf
			.select(Projections.constructor(
				FavoritesRes.class,
				content.id,
				favorite.id,
				content.title,
				contentImage.imageUrl,
				content.address,
				content.startDate,
				content.endDate
			))
			.from(favorite)
			.join(favorite.content, content)
			.leftJoin(contentImage).on(
				contentImage.id.eq(
					JPAExpressions
						.select(contentImageSub.id.min())
						.from(contentImageSub)
						.where(contentImageSub.content.eq(content))
				)
			)
			.where(builder)
			.orderBy(favorite.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = qf
			.select(favorite.count())
			.from(favorite)
			.join(favorite.content, content)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(contents, pageable, total != null ? total : 0);
	}

	@Override
	public Page<SchedulesInfoRes> findMySchedulesByUserId(Long userId, LocalDate day, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(schedule.user.id.eq(userId));
		builder.and(schedule.scheduleDate.eq(day));

		QContentImage contentImageSub = new QContentImage("contentImageSub");

		List<SchedulesInfoRes> schedules = qf
			.select(Projections.constructor(
				SchedulesInfoRes.class,
				content.id,
				content.title,
				contentImage.imageUrl,
				content.address,
				content.startDate,
				content.endDate
			))
			.from(schedule)
			.join(schedule.content, content)
			.leftJoin(contentImage).on(
				contentImage.id.eq(
					JPAExpressions
						.select(contentImageSub.id.min())
						.from(contentImageSub)
						.where(contentImageSub.content.eq(content))
				)
			)
			.where(builder)
			.orderBy(content.startDate.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = qf
			.select(content.count())
			.from(schedule)
			.join(schedule.content, content)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(schedules, pageable, total != null ? total : 0);
	}

	@Override
	public List<ContentRecommendRes> findRecommendedContents(ContentType contentType) {
		return qf
				.select(Projections.constructor(
						ContentRecommendRes.class,
						content.id,
						content.title,
						// 대표 이미지 url을 서브쿼리로 가져옴
						JPAExpressions.select(contentImageSub.imageUrl)
								.from(contentImageSub)
								.where(contentImageSub.content.eq(content))
								.orderBy(contentImageSub.id.asc())
								.limit(1),
						content.contentType,
						content.address,
						content.longitude,
						content.latitude,
						content.startDate.stringValue(),
						content.endDate.stringValue()
				))
				.from(content)
				.where(content.contentType.eq(contentType))
				.fetch();
  }
  
	@Override
	public List<LocalDate> findContentsByUserIdAndDateRange(Long userId, LocalDate start, LocalDate end) {
		return qf
			.select(schedule.scheduleDate)
			.from(schedule)
			.where(
				schedule.user.id.eq(userId),
				schedule.scheduleDate.between(start, end)
			)
			.fetch();
	}

	@Override
	public List<HotContentRes> findHotContentsThisMonth() {
		LocalDate now = LocalDate.now();
		LocalDate firstDay = now.withDayOfMonth(1);
		LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

		return qf.select(Projections.constructor(
						HotContentRes.class,
						content.id,
						content.title,
						JPAExpressions
								.select(contentImageSub.imageUrl)
								.from(contentImageSub)
								.where(contentImageSub.content.eq(content))
								.orderBy(contentImageSub.id.asc())
								.limit(1),
						content.contentType,
						content.address,
						content.longitude,
						content.latitude,
						content.startDate.stringValue(),
						content.endDate.stringValue()
				))
				.from(content)
				.where(content.startDate.goe(firstDay)
						.and(content.endDate.loe(lastDay)))
				.fetch();
	}

	@Override
	public List<WeeklyContentRes> findContentsByDate(LocalDate date) {
		return qf
				.select(Projections.constructor(
						WeeklyContentRes.class,
						content.id,
						content.title,
						contentImage.imageUrl.min(), // 최소 id에 해당하는 imageUrl
						content.address,
						content.startDate,
						content.endDate
				))
				.from(content)
				.leftJoin(contentImage).on(contentImage.content.eq(content))
				.where(content.startDate.loe(date)
						.and(content.endDate.goe(date)))
				.groupBy(content.id, content.title, content.address, content.startDate, content.endDate)
				.fetch();
	}

	@Override
	public List<ContentCategoryRes> findContentsByCategory(ContentType contentType) {
		return qf
				.select(Projections.constructor(
						ContentCategoryRes.class,
						content.id,
						content.title,
						contentImage.imageUrl.min(),
						content.longitude,
						content.latitude,
						content.startDate,
						content.endDate
				))
				.from(content)
				.leftJoin(contentImage).on(contentImage.content.eq(content))
				.where(contentType != null ? content.contentType.eq(contentType) : null) // contentType 없으면 전체 조회
				.groupBy(
						content.id,
						content.title,
						content.longitude,
						content.latitude,
						content.startDate,
						content.endDate
				)
				.fetch();
	}

	@Override
	public Page<SchedulesInfoRes> findSchedulesByDate(Pageable pageable, LocalDate day) {
		QContentImage contentImageSub = new QContentImage("contentImageSub");

		List<SchedulesInfoRes> schedules = qf
			.select(Projections.constructor(
				SchedulesInfoRes.class,
				content.id,
				content.title,
				contentImage.imageUrl,
				content.address,
				content.startDate,
				content.endDate
			))
			.from(schedule)
			.join(schedule.content, content)
			.leftJoin(contentImage).on(
				contentImage.id.eq(
					JPAExpressions
						.select(contentImageSub.id.min())
						.from(contentImageSub)
						.where(contentImageSub.content.eq(content))
				)
			)
			.where(
				content.startDate.loe(day),
				content.endDate.goe(day),
				content.status.eq(ContentStatus.ACTIVE)
			)
			.orderBy(content.endDate.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = qf
			.select(content.count())
			.from(schedule)
			.join(schedule.content, content)
			.where(
				content.startDate.loe(day),
				content.endDate.goe(day),
				content.status.eq(ContentStatus.ACTIVE)
			)
			.fetchOne();

		return new PageImpl<>(schedules, pageable, total != null ? total : 0);
	}

	@Override
	public List<ContentRegionRes> findRecommendedByUserRegion(Long userId) {
		// 서브쿼리로 사용자 지역 ID 조회
		QUser userSub = new QUser("userSub");

		return qf.select(Projections.constructor(
						ContentRegionRes.class,
						content.id,
						content.title,
						content.address,
						// 대표 이미지 서브쿼리 (예: contentImage가 있다면)
						JPAExpressions.select(contentImageSub.imageUrl)
								.from(contentImageSub)
								.where(contentImageSub.content.eq(content))
								.orderBy(contentImageSub.id.asc())
								.limit(1),
						content.startDate.stringValue(),
						content.endDate.stringValue()
				))
				.from(content)
				.join(content.region, region)
				// content.region.id가 사용자 지역 ID와 같은 것만 필터링
				.where(region.id.eq(
						JPAExpressions.select(userSub.region.id)
								.from(userSub)
								.where(userSub.id.eq(userId))
				))
				.fetch();
	}

}
