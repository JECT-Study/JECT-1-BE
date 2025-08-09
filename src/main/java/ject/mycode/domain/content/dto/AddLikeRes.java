package ject.mycode.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddLikeRes {
	Long likeId;
	Long likeCount;
}
