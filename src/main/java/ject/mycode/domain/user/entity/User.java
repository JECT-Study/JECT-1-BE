package ject.mycode.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import ject.mycode.domain.region.entity.Region;
import ject.mycode.domain.user.enums.SocialType;
import ject.mycode.domain.user.enums.UserRole;
import ject.mycode.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 추후 삭제 예정
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@Column(nullable = false)
	private String socialId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	@Column(unique = true, nullable = false)
	private String nickname;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(columnDefinition = "TEXT")
	private String image;

	//private String provider;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	private Region region;

	public void encodePassword(String password) {
		this.password = password;
	}
}
