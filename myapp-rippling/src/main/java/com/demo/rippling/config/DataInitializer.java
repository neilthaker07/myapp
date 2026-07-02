package com.demo.rippling.config;

import com.demo.rippling.entity.ProviderEntity;
import com.demo.rippling.entity.ProviderScheduleEntity;
import com.demo.rippling.repository.ProviderRepository;
import com.demo.rippling.repository.ProviderScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProviderRepository providerRepository;
    private final ProviderScheduleRepository scheduleRepository;

    @Override
    public void run(String... args) {
        if (providerRepository.existsByName("Dr. Sarah Chen")) {
            log.info("Providers already seeded — skipping data init");
            return;
        }

        List<ProviderEntity> providers = providerRepository.saveAll(List.of(
            provider("Dr. Sarah Chen",      38, 94102, new BigDecimal("0.94"), 2),
            provider("Dr. Marcus Johnson",  45, 10001, new BigDecimal("0.89"), 4),
            provider("Dr. Emily Rodriguez", 52, 60601, new BigDecimal("0.96"), 1),
            provider("Dr. James Park",      33, 77001, new BigDecimal("0.88"), 5),
            provider("Dr. Lisa Thompson",   41, 90001, new BigDecimal("0.92"), 3),
            provider("Dr. Robert Kim",      58, 98101, new BigDecimal("0.97"), 0),
            provider("Dr. Amanda Foster",   36, 85001, new BigDecimal("0.85"), 6),
            provider("Dr. David Martinez",  47, 30301, new BigDecimal("0.91"), 2),
            provider("Dr. Jennifer Walsh",  43, 2101,  new BigDecimal("0.93"), 1),
            provider("Dr. Michael Patel",   39, 78201, new BigDecimal("0.87"), 3)
        ));

        scheduleRepository.saveAll(List.of(
            schedule(providers.get(0), DayOfWeek.MONDAY,    "09:00", "13:00"),
            schedule(providers.get(0), DayOfWeek.WEDNESDAY, "09:00", "13:00"),
            schedule(providers.get(0), DayOfWeek.FRIDAY,    "09:00", "12:00"),

            schedule(providers.get(1), DayOfWeek.TUESDAY,   "10:00", "15:00"),
            schedule(providers.get(1), DayOfWeek.THURSDAY,  "10:00", "15:00"),

            schedule(providers.get(2), DayOfWeek.MONDAY,    "08:00", "12:00"),
            schedule(providers.get(2), DayOfWeek.TUESDAY,   "08:00", "12:00"),
            schedule(providers.get(2), DayOfWeek.WEDNESDAY, "08:00", "12:00"),
            schedule(providers.get(2), DayOfWeek.THURSDAY,  "08:00", "12:00"),

            schedule(providers.get(3), DayOfWeek.WEDNESDAY, "13:00", "17:00"),
            schedule(providers.get(3), DayOfWeek.FRIDAY,    "13:00", "17:00"),

            schedule(providers.get(4), DayOfWeek.MONDAY,    "09:00", "16:00"),
            schedule(providers.get(4), DayOfWeek.THURSDAY,  "09:00", "16:00"),

            schedule(providers.get(5), DayOfWeek.TUESDAY,   "07:00", "11:00"),
            schedule(providers.get(5), DayOfWeek.WEDNESDAY, "07:00", "11:00"),
            schedule(providers.get(5), DayOfWeek.FRIDAY,    "07:00", "11:00"),

            schedule(providers.get(6), DayOfWeek.MONDAY,    "14:00", "18:00"),
            schedule(providers.get(6), DayOfWeek.TUESDAY,   "14:00", "18:00"),

            schedule(providers.get(7), DayOfWeek.THURSDAY,  "09:00", "14:00"),
            schedule(providers.get(7), DayOfWeek.FRIDAY,    "09:00", "14:00"),

            schedule(providers.get(8), DayOfWeek.MONDAY,    "11:00", "16:00"),
            schedule(providers.get(8), DayOfWeek.WEDNESDAY, "11:00", "16:00"),

            schedule(providers.get(9), DayOfWeek.TUESDAY,   "08:00", "13:00"),
            schedule(providers.get(9), DayOfWeek.THURSDAY,  "08:00", "13:00")
        ));

        log.info("Seeded {} providers with schedules", providers.size());
    }

    private ProviderEntity provider(String name, int age, int zip,
                                    BigDecimal retention, int lateAppointments) {
        return ProviderEntity.builder()
                .name(name)
                .age(age)
                .zip(zip)
                .clientRetention(retention)
                .lateAppointments(lateAppointments)
                .build();
    }

    private ProviderScheduleEntity schedule(ProviderEntity provider, DayOfWeek day,
                                             String start, String end) {
        return ProviderScheduleEntity.builder()
                .provider(provider)
                .dayOfWeek(day)
                .startTime(LocalTime.parse(start))
                .endTime(LocalTime.parse(end))
                .build();
    }
}
