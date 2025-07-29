package ject.mycode.domain.search.repository;

import ject.mycode.domain.search.entity.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRepository extends JpaRepository<SearchKeyword, Long> {
    List<SearchKeyword> findTop10ByUserIdOrderBySearchedAtDesc(Long userId);
}