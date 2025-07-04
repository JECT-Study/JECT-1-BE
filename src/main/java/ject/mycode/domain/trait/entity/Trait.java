package ject.mycode.domain.trait.entity;

import jakarta.persistence.*;
import ject.mycode.domain.contentTrait.entity.ContentTrait;
import ject.mycode.domain.trait.enums.MinusTrait;
import ject.mycode.domain.trait.enums.PlusTrait;
import ject.mycode.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trait")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Trait extends BaseEntity {
	@Id
	@Column(name = "trait_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PlusTrait plusTrait;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MinusTrait minusTrait;

	@OneToMany(mappedBy = "trait", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ContentTrait> contentTraits = new ArrayList<>();
}
