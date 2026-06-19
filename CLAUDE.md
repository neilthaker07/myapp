# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is an interview-prep / hands-on learning project for **production-level Java Spring Boot patterns**. Each feature or module added here is a deliberate practice exercise — not a real product. The goal is to build familiarity with patterns commonly expected in senior Java interviews: layered architecture, dependency injection, REST conventions, service abstractions, and eventually persistence, security, and testing patterns.

When helping with this project, prefer demonstrating **idiomatic, production-quality** Spring Boot code even when a simpler approach would work. Explain why a pattern is used when it's non-obvious.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build (compile + package)
./mvnw package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyappApplicationTests
```

## Architecture

Spring Boot 4.1.0 REST API, Java 21, using `spring-boot-starter-webmvc` (servlet-based, not reactive).

**Package:** `com.demo.myapp`

- `model/Book.java` — plain POJO with `id`, `title`, `author`
- `service/BookService.java` — in-memory store using a `HashMap<Long, Book>` with an auto-incrementing `nextId`; no database or JPA
- `controller/BookController.java` — `@RestController` at `/api/books` wiring HTTP verbs to the service; returns `ResponseEntity` with explicit status codes

**Data flow:** Controller → Service → in-memory Map. State is lost on restart. There is no persistence layer.

**API base URL:** `http://localhost:8080/api/books`
