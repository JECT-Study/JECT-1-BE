package ject.mycode.domain.search.service;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.contentImage.repository.ContentImageRepository;
import ject.mycode.domain.contentImage.repository.custom.ContentImageQueryRepository;
import ject.mycode.domain.search.dto.ContentSummary;
import ject.mycode.domain.search.dto.SearchContentsRes;
import ject.mycode.domain.search.repository.custom.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchQueryRepository searchQueryRepository;
    private final ContentImageQueryRepository contentImageQueryRepository;
    private final ContentRepository contentRepository;

    @Override
    public SearchContentsRes searchContents(String keyword, int page, int limit, String sort) {
        int offset = (page - 1) * limit;
        // 검색 결과 조회
        List<Content> contentList = searchQueryRepository.findContentsByKeyword(keyword, limit, offset, sort);

        // 조회수 1씩 증가
        contentList.forEach(content -> content.setViews(content.getViews() + 1));

        // 변경된 조회수를 DB에 반영
        contentRepository.saveAll(contentList);

        // 전체 개수 조회
        int totalCount = searchQueryRepository.countContentsByKeyword(keyword);
        // contentId -> 썸네일 imageUrl 맵 만들기
        Map<Long, String> thumbnailMap = contentImageQueryRepository.findThumbnailUrlsByContentIds(
                contentList.stream().map(Content::getId).toList()
        );

        List<ContentSummary> summaries = contentList.stream()
                .map(content -> new ContentSummary(
                        content.getId(),
                        content.getTitle(),
                        thumbnailMap.get(content.getId()), // 썸네일 URL 매핑해서 넣기
                        content.getContentType().toString(),
                        content.getAddress(),
                        content.getStartDate(),
                        content.getViews()
                ))
                .toList();

        return new SearchContentsRes(totalCount, page, summaries);
    }
}
