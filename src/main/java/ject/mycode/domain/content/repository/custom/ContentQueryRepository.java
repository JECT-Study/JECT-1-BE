package ject.mycode.domain.content.repository.custom;

import ject.mycode.domain.content.dto.ContentDetailsRes;

public interface ContentQueryRepository {
	ContentDetailsRes findDetailsById(Long contentId);
}
