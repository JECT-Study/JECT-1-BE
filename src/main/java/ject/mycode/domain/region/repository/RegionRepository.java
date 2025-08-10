package ject.mycode.domain.region.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
	Optional<Region> findByName(String name);
}
