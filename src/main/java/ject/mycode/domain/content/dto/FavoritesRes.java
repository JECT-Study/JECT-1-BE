package ject.mycode.domain.content.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoritesRes {
	Long contentId;
	Long likeId;
	String title;
	String image;
	String address;
	LocalDate startDate;
	LocalDate endDate;
}
