package com.demo.rippling;

import lombok.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GrowPractise {
    public static void main(String[] args) {
        Map<Long, Provider> providersById = Map.of(
            1L, new Provider(1L, "Dr. Alice Kim",    94102, 4, new BigDecimal("0.91"), 45, List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), 0.0),
            2L, new Provider(2L, "Dr. Ben Torres",   10001, 2, new BigDecimal("0.91"), 35, List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY), 0.0),
            3L, new Provider(3L, "Dr. Clara Nguyen", 60601, 5, new BigDecimal("0.95"), 50, List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY), 0.0)
        );

        Map<Long, Client> clientsById = Map.of(
            1L, new Client(1L, "John Smith",   40, List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)),
            2L, new Client(2L, "Maria Lopez",  50, List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)),
            3L, new Client(3L, "James Wright", 35, List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
        );

        // Appointment is the join entity — holds providerId + clientId as FKs
        Map<Long, Appointment> appointmentsById = Map.of(
            1L, new Appointment(1L, LocalDateTime.of(2024, 1, 10, 9,  0), LocalDateTime.of(2024, 1, 10, 10, 0), AppointmentStatus.COMPLETED, 1L, 1L),
            2L, new Appointment(2L, LocalDateTime.of(2024, 1, 15, 14, 0), LocalDateTime.of(2024, 1, 15, 15, 0), AppointmentStatus.COMPLETED, 1L, 1L),
            3L, new Appointment(3L, LocalDateTime.of(2024, 2,  5, 11, 0), LocalDateTime.of(2024, 2,  5, 12, 0), AppointmentStatus.SCHEDULED, 2L, 2L),
            4L, new Appointment(4L, LocalDateTime.of(2024, 2, 20,  9, 0), LocalDateTime.of(2024, 2, 20, 10, 0), AppointmentStatus.CANCELLED, 2L, 2L),
            5L, new Appointment(5L, LocalDateTime.of(2024, 3,  1,  9, 0), LocalDateTime.of(2024, 3,  1, 10, 0), AppointmentStatus.CANCELLED, 3L, 3L),
            6L, new Appointment(6L, LocalDateTime.of(2024, 3,  8, 13, 0), LocalDateTime.of(2024, 3,  8, 14, 0), AppointmentStatus.CANCELLED, 3L, 3L)
        );

        rankProviders(providersById, appointmentsById).forEach(System.out::println);
        rankClients(clientsById, appointmentsById).forEach(System.out::println);

        System.out.println("\n--- Provider matches for John Smith ---");
        matchProviders(clientsById.get(1L), providersById).forEach(System.out::println);
    }

    static List<Provider> rankProviders(Map<Long, Provider> providersById, Map<Long, Appointment> appointmentsById) {
        return providersById.values().stream()
                .map(p -> {
                    long cancelled = appointmentsById.values().stream()
                            .filter(a -> a.getProviderId().equals(p.getProviderId())
                                      && a.getStatus() == AppointmentStatus.CANCELLED)
                            .count();
                    p.setRankScore(
                        p.getClientRetention().doubleValue() * 100
                        - p.getLateAppointments() * 10
                        - cancelled * 5
                    );
                    return p;
                })
                .sorted(Comparator.comparingDouble(Provider::getRankScore).reversed())
                .toList();
    }

    static List<Client> rankClients(Map<Long, Client> clientsById, Map<Long, Appointment> appointmentsById) {
        return clientsById.values().stream().sorted(
                Comparator.comparingLong(
                        c -> appointmentsById.values().stream()
                                .filter(a -> a.getClientId().equals(c.getClientId()))
                                .count()
                )
        ).toList();
    }

    // Filter to providers sharing at least one preferred day, then rank by
    // most overlapping days (desc) and closest age (asc). Return top 3.
    static List<Provider> matchProviders(Client client, Map<Long, Provider> providersById) {
        return providersById.values().stream()
                .filter(p -> !Collections.disjoint(p.getAvailabilities(), client.getPreferredDays()))
                .sorted(Comparator
                        .comparingLong((Provider p) -> p.getAvailabilities().stream()
                                .filter(client.getPreferredDays()::contains)
                                .count()).reversed()
                        .thenComparingInt(p -> Math.abs(p.getAge() - client.getPreferredProviderAge())))
                .limit(3)
                .toList();
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Provider {
    Long providerId;
    String name;
    Integer zip;
    Integer lateAppointments;
    BigDecimal clientRetention;
    Integer age;
    List<DayOfWeek> availabilities;
    double rankScore;

    @Override
    public String toString() {
        return providerId + " " + name + " (score=" + rankScore + ", age=" + age + ", days=" + availabilities + ")";
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Appointment {
    Long appointmentId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    AppointmentStatus status;
    Long providerId;
    Long clientId;
}

enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Client implements Comparable<Client> {
    Long clientId;
    String name;
    Integer preferredProviderAge;
    List<DayOfWeek> preferredDays;

    // natural order by name; use a Comparator when sorting by derived state (e.g. appointment count)
    @Override
    public int compareTo(Client other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return clientId + " " + name;
    }
}
