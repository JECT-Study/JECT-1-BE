package ject.mycode.domain.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.schedule.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
