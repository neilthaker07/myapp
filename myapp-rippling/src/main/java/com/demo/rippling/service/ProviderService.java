package com.demo.rippling.service;

import com.demo.rippling.dto.AddScheduleRequest;
import com.demo.rippling.dto.CreateProviderRequest;
import com.demo.rippling.dto.ProviderResponse;
import com.demo.rippling.dto.ScheduleResponse;
import com.demo.rippling.entity.AppointmentStatus;
import com.demo.rippling.entity.ProviderEntity;
import com.demo.rippling.entity.ProviderScheduleEntity;
import com.demo.rippling.repository.AppointmentRepository;
import com.demo.rippling.repository.ProviderRepository;
import com.demo.rippling.repository.ProviderScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private static final int SLOT_MINUTES = 30;

    private final ProviderRepository providerRepository;
    private final ProviderScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public List<ProviderResponse> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProviderResponse createProvider(CreateProviderRequest req) {
        ProviderEntity saved = providerRepository.save(ProviderEntity.builder()
                .name(req.name())
                .zip(req.zip())
                .lateAppointments(req.lateAppointments() != null ? req.lateAppointments() : 0)
                .clientRetention(req.clientRetention())
                .age(req.age())
                .build());
        return toResponse(saved);
    }

    public ScheduleResponse addSchedule(Long providerId, AddScheduleRequest req) {
        ProviderEntity provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new NoSuchElementException("Provider not found: " + providerId));

        ProviderScheduleEntity saved = scheduleRepository.save(ProviderScheduleEntity.builder()
                .provider(provider)
                .dayOfWeek(DayOfWeek.valueOf(req.dayOfWeek().toUpperCase()))
                .startTime(LocalTime.parse(req.startTime()))
                .endTime(LocalTime.parse(req.endTime()))
                .build());
        return toScheduleResponse(saved);
    }

    // Both age range and availability are hard filters — a provider must satisfy both to appear.
    public List<ProviderResponse> getMatchingProviders(LocalDate date, int preferredAge, int ageTolerance) {
        DayOfWeek day = date.getDayOfWeek();

        // hard requirement 1: provider age within [preferredAge - tolerance, preferredAge + tolerance]
        Set<Long> providerIdsWithSchedule = scheduleRepository.findByDayOfWeek(day).stream()
                .map(s -> s.getProvider().getProviderId())
                .collect(Collectors.toSet());

        // hard requirement 2: has at least one free slot on the requested date
        return providerRepository.findByAgeBetween(preferredAge - ageTolerance, preferredAge + ageTolerance)
                .stream()
                .filter(p -> providerIdsWithSchedule.contains(p.getProviderId()))
                .filter(p -> hasAvailableSlot(p.getProviderId(), day, date))
                .map(this::toResponse)
                .toList();
    }

    private boolean hasAvailableSlot(Long providerId, DayOfWeek day, LocalDate date) {
        Set<LocalTime> booked = appointmentRepository
                .findByProvider_ProviderIdAndStatusAndStartTimeBetween(
                        providerId, AppointmentStatus.SCHEDULED,
                        date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream()
                .map(a -> a.getStartTime().toLocalTime())
                .collect(Collectors.toSet());

        return scheduleRepository.findByProvider_ProviderIdAndDayOfWeek(providerId, day).stream()
                .anyMatch(s -> generateSlots(s.getStartTime(), s.getEndTime()).stream()
                        .anyMatch(slot -> !booked.contains(slot)));
    }

    private List<LocalTime> generateSlots(LocalTime start, LocalTime end) {
        List<LocalTime> slots = new java.util.ArrayList<>();
        LocalTime cursor = start;
        while (!cursor.plusMinutes(SLOT_MINUTES).isAfter(end)) {
            slots.add(cursor);
            cursor = cursor.plusMinutes(SLOT_MINUTES);
        }
        return slots;
    }

    private ProviderResponse toResponse(ProviderEntity e) {
        return new ProviderResponse(e.getProviderId(), e.getName(), e.getZip(),
                e.getAge(), e.getLateAppointments(), e.getClientRetention());
    }

    private ScheduleResponse toScheduleResponse(ProviderScheduleEntity e) {
        return new ScheduleResponse(e.getScheduleId(), e.getProvider().getProviderId(),
                e.getDayOfWeek().name(), e.getStartTime().toString(), e.getEndTime().toString());
    }
}
