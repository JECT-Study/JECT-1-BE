package ject.mycode.domain.content.entity;

import jakarta.persistence.*;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.contentTrait.entity.ContentTrait;
import ject.mycode.domain.region.entity.Region;
import ject.mycode.global.entity.BaseEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "content")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Content extends BaseEntity {
    @Id
    @Column(name = "content_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String image;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String introduction;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private Boolean isAlwaysOpen;

    private LocalTime openingHour;

    private LocalTime closedHour;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentTrait> contentTraits;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
}
