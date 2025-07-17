package ject.mycode.domain.content.repository.custom;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.entity.QContent;
import ject.mycode.domain.contentImage.entity.QContentImage;
import ject.mycode.domain.favorite.entity.QFavorite;
import ject.mycode.domain.tag.entity.QContentTag;
import ject.mycode.domain.tag.entity.QTag;
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

	@Override
	public ContentDetailsRes findDetailsById(Long contentId) {
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
}
