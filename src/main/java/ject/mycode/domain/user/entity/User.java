package ject.mycode.domain.user.entity;

import jakarta.persistence.*;
import ject.mycode.domain.region.entity.Region;
import ject.mycode.domain.region.entity.UserRegion;
import ject.mycode.domain.user.enums.SocialType;
import ject.mycode.domain.user.enums.UserRole;
import ject.mycode.domain.user.enums.UserStatus;
import ject.mycode.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	@Column(nullable = false)
	private String socialId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialType socialType;

	@Column(unique = true, nullable = false)
	private String nickname;

	@Column(columnDefinition = "TEXT")
	private String image;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus userStatus;

	// 탈퇴한 시간 저장용
	private LocalDateTime inactiveDate;

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserRegion> userRegions = new ArrayList<>();

	public void changeProfileImage(String image) {
		this.image = image;
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public void setInactiveDate(LocalDateTime now) {
		this.inactiveDate = now;
	}
}
