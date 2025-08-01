package ject.mycode.domain.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

}
