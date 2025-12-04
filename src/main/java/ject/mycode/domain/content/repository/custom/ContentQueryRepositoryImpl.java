package ject.mycode.domain.content.repository.custom;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import com.querydsl.core.types.dsl.*;
import ject.mycode.domain.content.dto.*;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.contentTrait.entity.ContentTrait;
import ject.mycode.domain.contentTrait.repository.ContentTraitRepository;
import ject.mycode.domain.region.entity.QUserRegion;
import ject.mycode.domain.region.repository.UserRegionRepository;
import ject.mycode.domain.trait.entity.UserTrait;
import ject.mycode.domain.trait.repository.UserTraitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
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
	private final QUserRegion userRegion = QUserRegion.userRegion;
    private final UserRegionRepository userRegionRepository;
    private final UserTraitRepository userTraitRepository;
    private final ContentTraitRepository  contentTraitRepository;

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
					content.latitude,
					content.homepageUrl,
					content.telNumber
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
				content.latitude,
				content.homepageUrl,
				content.telNumber
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
    public List<ContentRecommendRes> findRecommendedContents(Long userId, ContentType contentType) {

        // 사용자 선호 지역 조회
        List<Long> preferredRegionIds = userRegionRepository.findAllByUserId(userId).stream()
                .map(ur -> ur.getRegion().getId())
                .collect(Collectors.toList());

        BooleanExpression regionFilter = preferredRegionIds.isEmpty() ? null : content.region.id.in(preferredRegionIds);

        // 콘텐츠 기본 리스트 조회
        List<Content> contents = qf.selectFrom(content)
                .where(content.contentType.eq(contentType), regionFilter)
                .fetch();

        // 사용자 성향 조회
        List<UserTrait> userTraits = userTraitRepository.findAllByUserId(userId);
        if (userTraits.isEmpty()) {
            // 성향 정보 없으면 종료일 기준 상위 9개 반환
            return contents.stream()
                    .sorted(Comparator.comparing(Content::getEndDate).reversed())
                    .limit(9)
                    .map(ContentRecommendRes::fromEntity)
                    .collect(Collectors.toList());
        }

        // 코사인 유사도 기반 추천 (Pair<Content, similarity>)
        List<Pair<Content, Double>> scoredContents = new ArrayList<>();

        for (Content c : contents) {
            List<ContentTrait> contentTraits = contentTraitRepository.findAllByContentId(c.getId());
            if (contentTraits.isEmpty()) continue;

            double dotProduct = 0;
            double userNorm = 0;
            double contentNorm = 0;

            for (UserTrait ut : userTraits) {
                Optional<ContentTrait> optCt = contentTraits.stream()
                        .filter(ct -> ct.getTrait().getId().equals(ut.getTrait().getId()))
                        .findFirst();

                int contentScore = optCt.map(ContentTrait::getTotalScore).orElse(0);
                int userScore = ut.getTotalScore();

                dotProduct += userScore * contentScore;
                userNorm += userScore * userScore;
                contentNorm += contentScore * contentScore;
            }

            if (userNorm == 0 || contentNorm == 0) continue;

            double similarity = dotProduct / (Math.sqrt(userNorm) * Math.sqrt(contentNorm));

            // 코사인 유사도 0 이상만 Pair로 저장
            if (similarity >= 0) {
                scoredContents.add(Pair.of(c, similarity));
            }
        }

        // 유사도 점수 기준 내림차순 정렬
        List<Pair<Content, Double>> top9 = scoredContents.stream()
                .sorted((p1, p2) -> Double.compare(p2.getSecond(), p1.getSecond())) // getSecond()
                .limit(9)
                .collect(Collectors.toList());

        // 상위 9개만 로그 찍기
        top9.forEach(p -> System.out.println(
                "Content ID: " + p.getFirst().getId() +
                        ", Title: " + p.getFirst().getTitle() +
                        ", Cosine Similarity: " + p.getSecond()
        ));

        // ContentRecommendRes로 변환 후 반환
        return top9.stream()
                .map(p -> ContentRecommendRes.fromEntity(p.getFirst()))
                .collect(Collectors.toList());
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

		LocalDate today = LocalDate.now();
		NumberExpression<Integer> statusOrder = new CaseBuilder()
				.when(content.endDate.goe(today)).then(0)
				.otherwise(1);

		return qf.select(Projections.constructor(
						HotContentRes.class,
						content.id,
						content.title,
						JPAExpressions
								.select(contentImageSub.imageUrl.min())
								.from(contentImageSub)
								.where(contentImageSub.content.eq(content)),
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
				.orderBy(
						statusOrder.asc(),
						content.endDate.asc(),
						content.startDate.asc()
				)
				.limit(9)
				.fetch();
	}

	@Override
	public List<WeeklyContentRes> findContentsByDate(LocalDate date) {
		LocalDate today = LocalDate.now();
		NumberExpression<Integer> statusOrder = new CaseBuilder()
				.when(content.endDate.goe(today)).then(0)
				.otherwise(1);

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
				.orderBy(
						statusOrder.asc(),
						content.endDate.asc(),
						content.startDate.asc()
				)
				.limit(9)
				.fetch();
	}

	@Override
	public List<ContentCategoryRes> findContentsByCategory(ContentType contentType) {
		LocalDate today = LocalDate.now();

		NumberExpression<Integer> statusOrder = new CaseBuilder()
				.when(content.endDate.after(today).or(content.endDate.eq(today))).then(0) // 진행중
				.otherwise(1); // 진행완료

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
				.orderBy(
						statusOrder.asc(), // 1차: 진행중 먼저
						content.endDate.asc(), // 2차: 종료일 빠른 순
						content.startDate.asc() // 3차: 시작일 빠른 순 (동일 종료일일 경우)
				)
				.limit(9)
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
                .distinct()
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
                .select(content.id.countDistinct())
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
        LocalDate today = LocalDate.now();

        // 1. 사용자 선호 지역 ID 목록 조회 (userId가 null이면 조회하지 않음)
        List<Long> preferredRegionIds = new ArrayList<>();
        if (userId != null) { // userId가 null이 아닐 때만 선호 지역 조회
            preferredRegionIds = userRegionRepository.findAllByUserId(userId).stream()
                    // UserRegion 엔티티에서 Region 엔티티의 ID를 추출
                    .map(userRegion -> userRegion.getRegion().getId())
                    .collect(Collectors.toList());
        }

        // 2. 지역 조건(BooleanExpression) 생성
        // userId가 null이거나 선호 지역이 설정되어 있을 때만 지역 필터를 적용합니다.
        BooleanExpression regionFilter = null;
        if (!preferredRegionIds.isEmpty()) {
            // content 엔티티에 region 필드가 있다고 가정하고, 해당 ID가 리스트에 포함되는 조건 생성
            regionFilter = content.region.id.in(preferredRegionIds);
        }

        // 3. 날짜 기반 정렬 로직 (기존과 동일)
        NumberExpression<Integer> statusOrder = new CaseBuilder()
                .when(content.endDate.goe(today)).then(0)
                .otherwise(1);

        // 4. Querydsl 쿼리 실행
        return qf.select(Projections.constructor(
                        ContentRegionRes.class,
                        content.id,
                        content.title,
                        JPAExpressions.select(contentImageSub.imageUrl.min())
                                .from(contentImageSub)
                                .where(contentImageSub.content.eq(content)),
                        content.address,
                        content.startDate.stringValue(),
                        content.endDate.stringValue()
                ))
                .from(content)
                // content.region.id를 사용했으므로, 조인이 암시적으로 발생합니다.
                // 명시적 JOIN은 필요하지 않으나, 엔티티 관계에 따라 유지할 수도 있습니다.
                // 여기서는 필터링만 regionFilter에 맡기므로, join(content.region, region)은 제거할 수 있습니다.
                // 단, 기존 코드에 명시적인 JOIN이 있었으므로, 명확성을 위해 유지합니다.
                .join(content.region, region)

                // 지역 필터 (null이면 무시됨, 값이 있으면 IN 조건으로 적용됨)
                .where(regionFilter)

                .orderBy(
                        statusOrder.asc(),
                        content.endDate.asc(),
                        content.startDate.asc()
                )
                .limit(9)
                .fetch();
    }
}
