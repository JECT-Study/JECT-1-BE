package ject.mycode.domain.contentTrait.entity;

import jakarta.persistence.*;
import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.trait.entity.Trait;
import ject.mycode.global.entity.BaseEntity;
import lombok.*;

@Entity
@Table(name = "content_trait")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContentTrait extends BaseEntity{
    @Id
    @Column(name = "content_trait_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int totalScore;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne
    @JoinColumn(name = "trait_id")
    private Trait trait;
}
