package com.demo.rippling.repository;

import com.demo.rippling.entity.AppointmentEntity;
import com.demo.rippling.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    // used by availability check — all SCHEDULED appointments on a given day
    List<AppointmentEntity> findByProvider_ProviderIdAndStatusAndStartTimeBetween(
            Long providerId, AppointmentStatus status,
            LocalDateTime from, LocalDateTime to);

    // used by conflict check at booking time
    boolean existsByProvider_ProviderIdAndStatusAndStartTime(
            Long providerId, AppointmentStatus status, LocalDateTime startTime);
}
