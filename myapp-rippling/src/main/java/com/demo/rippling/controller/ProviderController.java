package com.demo.rippling.controller;

import com.demo.rippling.dto.*;
import com.demo.rippling.service.AppointmentService;
import com.demo.rippling.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    private final AppointmentService appointmentService;

    @GetMapping
    public List<ProviderResponse> getAllProviders() {
        return providerService.getAllProviders();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProviderResponse createProvider(@RequestBody CreateProviderRequest req) {
        return providerService.createProvider(req);
    }

    @PostMapping("/{id}/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse addSchedule(@PathVariable Long id,
                                        @RequestBody AddScheduleRequest req) {
        return providerService.addSchedule(id, req);
    }

    // GET /api/providers/{id}/availability?date=2024-06-03
    @GetMapping("/{id}/availability")
    public AvailabilityResponse getAvailability(@PathVariable Long id,
                                                @RequestParam String date) {
        return appointmentService.getAvailableSlots(id, LocalDate.parse(date));
    }

    // GET /api/providers/match?date=2024-06-03&preferredAge=40&ageTolerance=10
    // Both filters are hard requirements — provider must satisfy age range AND have a free slot.
    @GetMapping("/match")
    public List<ProviderResponse> getMatch(@RequestParam String date,
                                           @RequestParam int preferredAge,
                                           @RequestParam(defaultValue = "10") int ageTolerance) {
        return providerService.getMatchingProviders(LocalDate.parse(date), preferredAge, ageTolerance);
    }
}
