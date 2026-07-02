package com.demo.rippling.service;

import com.demo.rippling.dto.AppointmentResponse;
import com.demo.rippling.dto.AvailabilityResponse;
import com.demo.rippling.dto.BookAppointmentRequest;
import com.demo.rippling.entity.AppointmentEntity;
import com.demo.rippling.entity.AppointmentStatus;
import com.demo.rippling.entity.ClientEntity;
import com.demo.rippling.entity.ProviderEntity;
import com.demo.rippling.repository.AppointmentRepository;
import com.demo.rippling.repository.ClientRepository;
import com.demo.rippling.repository.ProviderRepository;
import com.demo.rippling.repository.ProviderScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int SLOT_MINUTES = 30;

    private final ProviderRepository providerRepository;
    private final ClientRepository clientRepository;
    private final ProviderScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;

    public AvailabilityResponse getAvailableSlots(Long providerId, LocalDate date) {
        if (!providerRepository.existsById(providerId)) {
            throw new NoSuchElementException("Provider not found: " + providerId);
        }

        // Step 1: schedule windows for that day of week
        Set<LocalTime> booked = appointmentRepository
                .findByProvider_ProviderIdAndStatusAndStartTimeBetween(
                        providerId, AppointmentStatus.SCHEDULED,
                        date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream()
                .map(a -> a.getStartTime().toLocalTime())
                .collect(Collectors.toSet());

        // Step 2 + 3: generate slots from schedule windows, subtract booked
        List<String> slots = scheduleRepository
                .findByProvider_ProviderIdAndDayOfWeek(providerId, date.getDayOfWeek())
                .stream()
                .flatMap(s -> generateSlots(s.getStartTime(), s.getEndTime()).stream())
                .filter(slot -> !booked.contains(slot))
                .distinct()
                .sorted()
                .map(LocalTime::toString)
                .toList();

        return new AvailabilityResponse(providerId, date.toString(), slots);
    }

    @Transactional
    public AppointmentResponse bookAppointment(BookAppointmentRequest req) {
        LocalDate date = LocalDate.parse(req.date());
        LocalTime time = LocalTime.parse(req.startTime());
        LocalDateTime startTime = date.atTime(time);
        LocalDateTime endTime = startTime.plusMinutes(SLOT_MINUTES);

        ProviderEntity provider = providerRepository.findById(req.providerId())
                .orElseThrow(() -> new NoSuchElementException("Provider not found: " + req.providerId()));
        ClientEntity client = clientRepository.findById(req.clientId())
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + req.clientId()));

        // validate: slot must fall within a schedule window for that day
        boolean withinSchedule = scheduleRepository
                .findByProvider_ProviderIdAndDayOfWeek(req.providerId(), date.getDayOfWeek())
                .stream()
                .anyMatch(s -> !time.isBefore(s.getStartTime())
                            && !time.plusMinutes(SLOT_MINUTES).isAfter(s.getEndTime()));
        if (!withinSchedule) {
            throw new IllegalArgumentException(
                    "Slot " + time + " is outside provider schedule on " + date.getDayOfWeek());
        }

        // validate: no existing SCHEDULED appointment at that exact slot
        if (appointmentRepository.existsByProvider_ProviderIdAndStatusAndStartTime(
                req.providerId(), AppointmentStatus.SCHEDULED, startTime)) {
            throw new IllegalStateException("Slot " + startTime + " is already booked");
        }

        AppointmentEntity saved = appointmentRepository.save(AppointmentEntity.builder()
                .provider(provider)
                .client(client)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.SCHEDULED)
                .build());

        return toResponse(saved);
    }

    private List<LocalTime> generateSlots(LocalTime start, LocalTime end) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime cursor = start;
        while (!cursor.plusMinutes(SLOT_MINUTES).isAfter(end)) {
            slots.add(cursor);
            cursor = cursor.plusMinutes(SLOT_MINUTES);
        }
        return slots;
    }

    private AppointmentResponse toResponse(AppointmentEntity e) {
        return new AppointmentResponse(
                e.getAppointmentId(),
                e.getProvider().getProviderId(),
                e.getClient().getClientId(),
                e.getStartTime().toString(),
                e.getEndTime().toString(),
                e.getStatus().name());
    }
}
