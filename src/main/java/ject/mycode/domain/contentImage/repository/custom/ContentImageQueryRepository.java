package ject.mycode.domain.contentImage.repository.custom;

import java.util.List;
import java.util.Map;

public interface ContentImageQueryRepository {
    Map<Long, String> findThumbnailUrlsByContentIds(List<Long> contentIds);
}
