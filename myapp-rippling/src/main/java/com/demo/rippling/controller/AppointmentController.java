package com.demo.rippling.controller;

import com.demo.rippling.dto.AppointmentResponse;
import com.demo.rippling.dto.BookAppointmentRequest;
import com.demo.rippling.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse bookAppointment(@RequestBody BookAppointmentRequest req) {
        return appointmentService.bookAppointment(req);
    }
}
