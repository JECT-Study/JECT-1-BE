package ject.mycode.domain.search.entity;

import jakarta.persistence.*;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_keyword", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "keyword"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SearchKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String keyword;

    private LocalDateTime searchedAt;

    public SearchKeyword(User user, String keyword) {
        this.user = user;
        this.keyword = keyword;
    }
}
