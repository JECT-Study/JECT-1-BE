package ject.mycode.domain.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.content.entity.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
