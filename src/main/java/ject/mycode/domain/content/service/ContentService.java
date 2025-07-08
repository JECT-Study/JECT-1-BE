package ject.mycode.domain.content.service;

import ject.mycode.domain.user.entity.User;

public interface ContentService {
	Long addFavorite(User user, Long contentId);
}
