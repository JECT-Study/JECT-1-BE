package ject.mycode.domain.search.service;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.contentImage.repository.custom.ContentImageQueryRepository;
import ject.mycode.domain.search.dto.*;
import ject.mycode.domain.search.repository.SearchRepository;
import ject.mycode.domain.search.repository.custom.SearchQueryRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.search.entity.SearchKeyword;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchQueryRepository searchQueryRepository;
    private final ContentImageQueryRepository contentImageQueryRepository;
    private final ContentRepository contentRepository;
    private final SearchRepository searchRepository;
    private final UserRepository userRepository;

    @Override
    public SearchContentsRes searchContents(String keyword, int page, int limit, String sort, User user) {
        int offset = (page - 1) * limit;
        List<Content> contentList;
        int totalCount;


        if (keyword == null || keyword.trim().isEmpty()) {
            contentList = searchQueryRepository.findAllContents(limit, offset, sort);
            totalCount = searchQueryRepository.countAllContents();
        } else {
            // ✅ 로그인 한 경우에만 검색 기록 저장
            if (user != null) {
                Optional<SearchKeyword> existingKeyword = searchRepository.findByUserIdAndKeyword(user.getId(), keyword);

                if (existingKeyword.isPresent()) {
                    SearchKeyword keywordEntity = existingKeyword.get();
                    keywordEntity.setSearchedAt(LocalDateTime.now());
                    searchRepository.save(keywordEntity);
                } else {
                    searchRepository.save(
                            SearchKeyword.builder()
                                    .user(user)
                                    .keyword(keyword)
                                    .searchedAt(LocalDateTime.now())
                                    .build()
                    );
                }
            }

            contentList = searchQueryRepository.findContentsByKeyword(keyword, limit, offset, sort);
            totalCount = searchQueryRepository.countContentsByKeyword(keyword);
        }

        // 조회수 +1
        contentList.forEach(content -> content.setViews(content.getViews() + 1));
        contentRepository.saveAll(contentList);

        // 썸네일 매핑
        Map<Long, String> thumbnailMap = contentImageQueryRepository.findThumbnailUrlsByContentIds(
                contentList.stream().map(Content::getId).toList()
        );

        List<ContentSummary> summaries = contentList.stream()
                .map(content -> new ContentSummary(
                        content.getId(),
                        content.getTitle(),
                        thumbnailMap.get(content.getId()),
                        content.getContentType().toString(),
                        content.getAddress(),
                        content.getStartDate(),
                        content.getViews()
                ))
                .toList();

        return new SearchContentsRes(totalCount, page, summaries);
    }


    @Override
    public List<String> getRecentSearchKeywords(User user) {

        userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(BaseResponseCode.USER_NOT_FOUND.getMessage()));

        // user별 최근 검색어 10개 조회 (가장 최근 순)
        return searchRepository.findTop10ByUserIdOrderBySearchedAtDesc(user.getId())
                .stream()
                .map(SearchKeyword::getKeyword)
                .toList();
    }


    @Transactional
    public void deleteKeyword(User user, String keyword) {

        userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(BaseResponseCode.USER_NOT_FOUND.getMessage()));

        SearchKeyword searchKeyword = searchRepository
                .findByUserIdAndKeyword(user.getId(), keyword)
                .orElseThrow(() -> new CustomException(BaseResponseCode.SEARCH_KEYWORD_NOT_FOUND));

        searchRepository.delete(searchKeyword);
    }

    @Transactional
    public void deleteAllKeywords(User user) {

        userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(BaseResponseCode.USER_NOT_FOUND.getMessage()));

        List<SearchKeyword> keywords = searchRepository.findAllByUserId(user.getId());
        if (keywords.isEmpty()) {
            throw new CustomException(BaseResponseCode.SEARCH_KEYWORD_NOT_FOUND);
        }
        searchRepository.deleteAll(keywords);
    }

    public List<String> getPopularKeywords() {
        return searchQueryRepository.findTop10PopularKeywords();
    }

    public SearchResultRes getSearchResults(String keyword, ContentType category, String region, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Content> contentPage = searchQueryRepository.getSearchResults(keyword, category, region, pageable);

        List<Content> contentList = contentPage.getContent();
        // 썸네일 map 조회
        Map<Long, String> thumbnailMap = contentImageQueryRepository.findThumbnailUrlsByContentIds(
                contentList.stream().map(Content::getId).toList()
        );

        List<ContentResultRes> dtos = contentPage.getContent().stream()
                .map(c -> new ContentResultRes(
                        c.getId(),
                        c.getTitle(),
                        c.getContentType(),
                        c.getAddress(),
                        thumbnailMap.get(c.getId())
                ))
                .toList();

        PageInfoRes pageInfo = new PageInfoRes(
                page,
                contentPage.getTotalPages(),
                contentPage.getTotalElements());

        return new SearchResultRes(dtos, pageInfo);
    }
}
