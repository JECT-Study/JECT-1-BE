package ject.mycode.domain.contentImage.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ject.mycode.domain.contentImage.entity.QContentImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ContentImageQueryRepositoryImpl implements ContentImageQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Map<Long, String> findThumbnailUrlsByContentIds(List<Long> contentIds) {
        QContentImage contentImage = QContentImage.contentImage;
        QContentImage contentImageSub = new QContentImage("contentImageSub");

        return queryFactory
                .select(Projections.tuple(
                        contentImage.content.id,
                        contentImage.imageUrl
                ))
                .from(contentImage)
                .where(
                        contentImage.id.eq(
                                JPAExpressions
                                        .select(contentImageSub.id.min())
                                        .from(contentImageSub)
                                        .where(contentImageSub.content.id.eq(contentImage.content.id))
                        ),
                        contentImage.content.id.in(contentIds)
                )
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(contentImage.content.id),
                        tuple -> tuple.get(contentImage.imageUrl)
                ));
    }

}
