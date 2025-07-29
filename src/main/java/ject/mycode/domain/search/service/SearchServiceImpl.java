package ject.mycode.domain.search.service;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.contentImage.repository.custom.ContentImageQueryRepository;
import ject.mycode.domain.search.dto.ContentSummary;
import ject.mycode.domain.search.dto.SearchContentsRes;
import ject.mycode.domain.search.repository.SearchRepository;
import ject.mycode.domain.search.repository.custom.SearchQueryRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.search.entity.SearchKeyword;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
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

        // 로그인 기능 완성되면 삭제될 예정
        Long userIdToSave = (user != null) ? user.getId() : 1L; // null이면 1L로 대체

        User userToSave = userRepository.findById(userIdToSave)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));


        Optional<SearchKeyword> existingKeyword = searchRepository.findByUserIdAndKeyword(userIdToSave, keyword);

        if (existingKeyword.isPresent()) {
            // 기존 검색어가 있으면 searchedAt만 업데이트
            SearchKeyword keywordEntity = existingKeyword.get();
            keywordEntity.setSearchedAt(LocalDateTime.now());
            searchRepository.save(keywordEntity);
        } else {
            // 없으면 새로 저장
            searchRepository.save(
                    SearchKeyword.builder()
                            .user(userToSave)
                            .keyword(keyword)
                            .searchedAt(LocalDateTime.now())
                            .build()
            );
        }
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

        // DTO 변환
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

    @Override
    public List<String> getRecentSearchKeywords(User user) {

        // 로그인 기능 완성되면 삭제될 예정
        Long userIdToSave = (user != null) ? user.getId() : 1L; // null이면 1L로 대체

        // user별 최근 검색어 10개 조회 (가장 최근 순)
        return searchRepository.findTop10ByUserIdOrderBySearchedAtDesc(userIdToSave)
                .stream()
                .map(SearchKeyword::getKeyword)
                .toList();
    }


    @Transactional
    public void deleteKeyword(User user, String keyword) {
        // 로그인 기능 완성되면 삭제될 예정
        Long userIdToSave = (user != null) ? user.getId() : 1L; // null이면 1L로 대체

        SearchKeyword searchKeyword = searchRepository
                .findByUserIdAndKeyword(userIdToSave, keyword)
                .orElseThrow(() -> new CustomException(BaseResponseCode.SEARCH_KEYWORD_NOT_FOUND));

        searchRepository.delete(searchKeyword);
    }

    @Transactional
    public void deleteAllKeywords(User user) {
        // 로그인 기능 완성되면 삭제될 예정
        Long userIdToSave = (user != null) ? user.getId() : 1L; // null이면 1L로 대체

        List<SearchKeyword> keywords = searchRepository.findAllByUserId(userIdToSave);
        if (keywords.isEmpty()) {
            throw new CustomException(BaseResponseCode.SEARCH_KEYWORD_NOT_FOUND);
        }
        searchRepository.deleteAll(keywords);
    }


    public List<String> getPopularKeywords() {
        return searchQueryRepository.findTop10PopularKeywords();
    }
}
