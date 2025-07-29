package ject.mycode.domain.search.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.entity.QContent;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.search.entity.QSearchKeyword;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
                .selectFrom(content)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public int countContentsByKeyword(String keyword) {
        BooleanExpression condition = content.title.containsIgnoreCase(keyword)
                .or(content.address.containsIgnoreCase(keyword));

        return Math.toIntExact(queryFactory
                .select(content.count())
                .from(content)
                .where(condition)
                .fetchOne());
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("views".equalsIgnoreCase(sort)) {
            return content.views.desc();
        }
        return content.createdAt.desc();
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
    public Page<Content> getSearchResults(String keyword, ContentType category, String region, Pageable pageable)  {

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(content.title.containsIgnoreCase(keyword)
                    .or(content.address.containsIgnoreCase(keyword)));
        }
        if (category != null) {
            builder.and(content.contentType.eq(category));
        }
        if (region != null && !region.isBlank()) {
            builder.and(content.address.containsIgnoreCase(region));
        }

        List<Content> results = queryFactory
                .selectFrom(content)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(content)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
