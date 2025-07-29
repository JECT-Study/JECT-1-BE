package ject.mycode.domain.search.repository.custom;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.entity.QContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchQueryRepositoryImpl implements SearchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QContent content = QContent.content;

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
}
