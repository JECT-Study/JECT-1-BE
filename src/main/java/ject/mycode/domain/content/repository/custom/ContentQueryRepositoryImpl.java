package ject.mycode.domain.content.repository.custom;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.entity.QContent;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.contentImage.entity.QContentImage;
import ject.mycode.domain.favorite.entity.QFavorite;
import ject.mycode.domain.schedule.entity.QSchedule;
import ject.mycode.domain.tag.entity.QContentTag;
import ject.mycode.domain.tag.entity.QTag;
import ject.mycode.domain.user.dto.MySchedulesRes;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ContentQueryRepositoryImpl implements ContentQueryRepository {

	private final JPAQueryFactory qf;
	private final QContent content = QContent.content;
	private final QContentImage contentImage = QContentImage.contentImage;
	private final QContentTag contentTag = QContentTag.contentTag;
	private final QTag tag = QTag.tag;
	private final QFavorite favorite = QFavorite.favorite;
	private final QSchedule schedule = QSchedule.schedule;

	@Override
	public ContentDetailsRes findDetailsByContentId(Long contentId) {
		// TODO: 후에 날쿼리로 리팩토링
		return qf.select(Projections.constructor(ContentDetailsRes.class,
				content.id,
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
	public Page<MySchedulesRes> findMySchedulesByUserId(Long userId, LocalDate day, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(schedule.user.id.eq(userId));
		builder.and(schedule.scheduleDate.eq(day));

		QContentImage contentImageSub = new QContentImage("contentImageSub");

		List<MySchedulesRes> schedules = qf
			.select(Projections.constructor(
				MySchedulesRes.class,
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
	public List<LocalDate> findContentsByUserIdAndDateRange(Long userId, LocalDate start, LocalDate end) {
		return qf
			.select(schedule.scheduleDate) // 바로 날짜만!
			.from(schedule)
			.where(
				schedule.user.id.eq(userId),
				schedule.scheduleDate.between(start, end)
			)
			.fetch();
	}

}
