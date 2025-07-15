package ject.mycode.domain.contentImage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.contentImage.entity.ContentImage;

public interface ContentImageRepository extends JpaRepository<ContentImage, Long> {
	List<ContentImage> findAllByContentId(Long contentId);
}
