package com.demo.rippling.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/rippling")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Health check requested");
        return ResponseEntity.ok(Map.of(
                "service", "myapp-rippling",
                "status", "UP",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        log.info("Hello endpoint requested");
        return ResponseEntity.ok(Map.of(
                "message", "Hello from the Rippling service",
                "port", "8081"
        ));
    }
}
