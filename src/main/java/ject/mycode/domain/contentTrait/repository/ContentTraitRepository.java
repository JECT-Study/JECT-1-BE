package ject.mycode.domain.contentTrait.repository;

import ject.mycode.domain.contentTrait.entity.ContentTrait;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentTraitRepository extends JpaRepository<ContentTrait, Long> {
    List<ContentTrait> findAllByContentId(Long contentId);
}
