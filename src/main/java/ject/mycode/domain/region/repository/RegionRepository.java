package ject.mycode.domain.region.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
	List<Region> findByNameIn(List<String> name);
}
