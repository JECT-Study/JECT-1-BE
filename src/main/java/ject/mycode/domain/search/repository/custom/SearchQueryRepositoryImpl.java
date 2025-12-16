package ject.mycode.domain.search.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.entity.QContent;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.search.entity.QSearchKeyword;
import ject.mycode.domain.user.enums.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchQueryRepositoryImpl implements SearchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QContent content = QContent.content;

    private final QSearchKeyword searchKeyword = QSearchKeyword.searchKeyword;

    @Override
    public List<Content> findContentsByKeyword(String keyword, int limit, int offset, String sort) {
        BooleanExpression condition = content.title.containsIgnoreCase(keyword)
                .or(content.address.containsIgnoreCase(keyword));

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sort);

        return queryFactory
                .selectDistinct(content)   // ⭐ 중복 해결 핵심
                .from(content)
                .where(
                        content.status.eq(ContentStatus.ACTIVE)
                                .and(isUpcomingEvent())  // ⭐ 추가
                                .and(condition)
                )
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public int countContentsByKeyword(String keyword) {
        BooleanExpression condition = content.title.containsIgnoreCase(keyword)
                .or(content.address.containsIgnoreCase(keyword));

        return Math.toIntExact(
                queryFactory
                .select(content.id.countDistinct())   // ⭐ ID 중복 제거하여 카운트
                .from(content)
                .where(condition)
                .fetchOne());
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        switch (sort != null ? sort.toLowerCase() : "date") {  // 기본값 date
            case "date":
                return content.startDate.asc();  // ⭐ 임박 순 (가까운 날짜 먼저)
            case "views":
                return content.views.desc();
            default:
                return content.startDate.asc();  // 기본: 날짜 임박 순
        }
    }

    @Override
    public List<String> findTop10PopularKeywords() {
        return queryFactory
                .select(searchKeyword.keyword)
                .from(searchKeyword)
                .groupBy(searchKeyword.keyword)
                .orderBy(searchKeyword.keyword.count().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public Page<Content> getSearchResults(String keyword, ContentType category, List<String> regions, Pageable pageable)  {

        QContent content = QContent.content;

        BooleanBuilder builder = new BooleanBuilder();

        // 1. 키워드 조건 추가
        if (keyword != null && !keyword.isBlank()) {
            builder.and(content.title.containsIgnoreCase(keyword)
                    .or(content.address.containsIgnoreCase(keyword)));
        }

        // 2. 카테고리 조건 추가
        if (category != null) {
            builder.and(content.contentType.eq(category));
        }

        // 3. 다중 지역(Regions) 조건 추가
        if (regions != null && !regions.isEmpty()) {
            BooleanBuilder regionBuilder = new BooleanBuilder();

            // regions 리스트에 있는 모든 지역에 대해 OR 조건을 생성합니다.
            for (String region : regions) {
                // 주소에 해당 지역명이 포함되는 경우 (기존 로직 유지)
                regionBuilder.or(content.address.containsIgnoreCase(region));
            }

            // 전체 쿼리 빌더에 지역 조건 추가
            builder.and(regionBuilder);
        }

        // 4. 데이터 조회 (페이징 적용)
        List<Content> results = queryFactory
                .selectFrom(content)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 5. 전체 개수 조회
        long total = queryFactory
                .select(content.id.countDistinct())  // ⭐ 중복 방지
                .from(content)
                .where(builder)
                .fetchCount();

        // 6. Page 객체 반환
        return new PageImpl<>(results, pageable, total);
    }

    public List<Content> findAllContents(int limit, int offset, String sort) {
        QContent content = QContent.content;

        OrderSpecifier<?> orderSpecifier;
        if ("views".equalsIgnoreCase(sort)) {
            orderSpecifier = content.views.desc();
        } else if ("date".equalsIgnoreCase(sort)) {
            orderSpecifier = content.startDate.desc();
        } else {
            orderSpecifier = content.id.desc(); // 기본 정렬
        }

        return queryFactory
                .selectDistinct(content)  // 혹시 모를 중복 예방
                .from(content)
                .where(
                        content.status.eq(ContentStatus.ACTIVE)  // 기존
                                .and(isUpcomingEvent())                // ⭐ 추가
                )
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public int countAllContents() {
        QContent content = QContent.content;
        return Math.toIntExact(
                queryFactory
                        .select(content.id.countDistinct()) // 중복예방
                        .from(content)
                        .fetchOne()
        );
    }

    private BooleanExpression isUpcomingEvent() {
        LocalDate now = LocalDate.now();
        return content.startDate.goe(now)
                .or(content.endDate.goe(now));
    }

}
