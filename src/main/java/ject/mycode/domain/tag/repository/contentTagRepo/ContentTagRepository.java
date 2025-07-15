package ject.mycode.domain.tag.repository.contentTagRepo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.tag.entity.ContentTag;

public interface ContentTagRepository extends JpaRepository<ContentTag, Long> {
	List<ContentTag> findAllByContentId(Long contentId);
}
