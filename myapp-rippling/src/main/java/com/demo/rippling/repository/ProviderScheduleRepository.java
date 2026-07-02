package com.demo.rippling.repository;

import com.demo.rippling.entity.ProviderScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ProviderScheduleRepository extends JpaRepository<ProviderScheduleEntity, Long> {

    List<ProviderScheduleEntity> findByProvider_ProviderIdAndDayOfWeek(Long providerId, DayOfWeek dayOfWeek);

    List<ProviderScheduleEntity> findByDayOfWeek(DayOfWeek dayOfWeek);
}
