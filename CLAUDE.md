# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is an interview-prep / hands-on learning project for production-level Java Spring Boot patterns. Each feature or module is a deliberate practice exercise, not a real product. The goal is to build familiarity with patterns commonly expected in senior Java interviews: layered architecture, dependency injection, REST conventions, service abstractions, and eventually persistence, security, and testing patterns.

**When helping with this project:**
- Prefer idiomatic, production-quality Spring Boot code even when a simpler approach would work.
- Explain why a pattern is used when it's non-obvious.
- Favor Java 8+ functional style: streams, `map`/`filter`/`reduce`, method references, `Optional` chaining, and `@FunctionalInterface` lambdas over imperative loops where it reads clearly. Show both the stream pipeline and what it replaces when the translation is instructive.

## Overview

This is a multi-module Spring Boot 4.1.0 / Java 21 interview-prep sandbox. Each module is a **standalone Spring Boot app** (not a Maven multi-module aggregator) — each has its own `pom.xml` and must be built/run independently.

| Module | Port | Purpose |
|---|------|---|
| `myapp` (root) | 8080 | Library management — dense design pattern showcase |
| `myapp-parking` | 8082 | Parking lot system — OOP interview problem |
| `myapp-rippling` | 8081 | Stub (health endpoint only) |

## Commands

Run from within the module directory (or pass `-f` with the module path).

**Build:**
```bash
./mvnw clean package -DskipTests          # root module
cd myapp-parking && ../mvnw clean package -DskipTests
```

**Run:**
```bash
./mvnw spring-boot:run                    # root module (port 8080)
cd myapp-parking && ../mvnw spring-boot:run  # port 8082
```

**Test:**
```bash
./mvnw test                               # all tests in a module
./mvnw test -Dtest=MyappApplicationTests  # single test class
```

## Root module — `myapp`

### Profile / database

`application.properties` defaults to `spring.profiles.active=postgres`, which requires PostgreSQL at `localhost:5432/library` (user/pass: `postgres`). Switch to in-memory storage with no DB required:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=memory"
```

Two `BookRepository` implementations are swapped via `@Profile`:
- `@Profile("memory")` → `InMemoryBookRepository` (HashMap)
- `@Profile("postgres")` → `PostgresBookRepository` (JPA + `BookEntity`)

### Request flow

```
BookController
  → LibraryFacade            (Facade — sole entry point into the service layer)
    → CachingBookServiceProxy (Proxy — @Primary IBookService; caches getBookById)
      → BookService           (real subject; holds the validation chain)
        → BookRepository      (swapped by @Profile)
```

`LibraryFacade` is where most pattern orchestration happens: it selects Strategy, composes Specifications, dispatches Commands, and publishes Observer events.

### Design patterns implemented

| Pattern | Where |
|---|---|
| Singleton (double-checked locking) | `AppLogger` |
| Builder | `Book.Builder` |
| Null Object | `NullBook.getInstance()` — callers use `book.isEmpty()` instead of null checks |
| Proxy (caching) | `CachingBookServiceProxy` — `@Primary` wraps `BookService`; invalidates on mutating ops |
| Facade | `LibraryFacade` — `BookController` talks only to this |
| State | `BookState` hierarchy; `Book` is the context; transitions via `book.lend()`, `book.returnBook()`, etc. |
| Command + Undo | `Command` / `BookCommandHistory` — `LibraryFacade.transitionState()` records commands; `undoLastAction()` reverses them |
| Observer | `BookEventPublisher` + `BookEventObserver` (@FunctionalInterface — observers can be lambdas) |
| Chain of Responsibility | `BookRequestHandler` chain (`DuplicateTitleHandler → MaxLibrarySizeHandler`) — built in `BookService` constructor using supplier lambdas so checks always read live data |
| Strategy | `BookSortStrategy` (@FunctionalInterface) — inline lambdas in `LibraryFacade.getBooks()` |
| Specification | `Specification<Book>` with `.and()` composition in `LibraryFacade.getFilteredBooks()` |
| Decorator | `coffee/` (CoffeeDecorator chain) and `decorator/` (BookFilter chain — compare with Specification) |
| Adapter | `BookToExternalAdapter` — used via `BookProjector<T>` constructor reference |
| Template Method | `BookReportTemplate` — `SummaryReportGenerator`, `DetailedReportGenerator`, `CsvReportGenerator` |
| Abstract Factory | `car/` — `CarFactory` → `TeslaFactory`, `PriusFactory`, `VolvoFactory` each produce matched `Engine` + `Body` |
| Mediator | `chat/` — `ChatRoom` implements `ChatMediator` |
| AOP | `PerformanceAspect` — timing pointcut across service layer |

### Functional programming style

This codebase leans into Java 8+ functional features throughout — follow the same style when adding code:

- **Streams over loops** — `list.stream().filter(...).map(...).toList()` is preferred over `for` + `if` + `add`.
- **`Optional` chaining** — use `.map()`, `.orElseGet()`, `.orElseThrow()` rather than unwrapping with `isPresent()` / `get()`.
- **Method references** — prefer `BookMapper::toResponse` over `b -> BookMapper.toResponse(b)`; prefer constructor references (`BookToExternalAdapter::new`) to supply projector lambdas.
- **`@FunctionalInterface` for strategy/observer injection** — `BookSortStrategy` and `BookEventObserver` are both `@FunctionalInterface`, so callers can pass inline lambdas instead of anonymous classes or dedicated implementations (see `LibraryFacade.getBooks()` and the observer registrations in the `LibraryFacade` constructor).
- **Switch expressions** — use `switch (x) { case "a" -> ...; }` (Java 14+ expression form) over `switch` statements with `break`.
- **`Comparator` composition** — `Comparator.comparing(Book::getTitle).thenComparing(Book::getAuthor)` rather than manual `compareTo` logic.
- **`Specification.and()`** — the `Specification<T>` interface in this project has a default `and()` method that composes predicates functionally; use it to build filters instead of nested `if` blocks (see `LibraryFacade.getFilteredBooks()`).

### API endpoints (port 8080)

```
POST   /api/books                     create book
GET    /api/books?sort=title|author|id list all
GET    /api/books/{id}                get one
PUT    /api/books/{id}                update
DELETE /api/books/{id}                delete
GET    /api/books/{id}/status         current BookStatus
PATCH  /api/books/{id}/status?action= state transition (lend|return|read-in-library|available-to-lend|remove)
POST   /api/books/{id}/undo           undo last state transition
GET    /api/books/{id}/history        command audit log
GET    /api/books/filter?genre=&language=&status=  Specification-based filter
GET    /api/books/report?format=summary|detailed|csv
GET    /api/books/{id}/external       Adapter view
```

## `myapp-parking` module

**No database** — all state is in-memory (`ConcurrentHashMap` for active tickets; `EnumMap<SpotType, EnumMap<SpotSize, Deque<ParkingSpot>>>` for the available-spot pool).

Uses **Lombok** (`@Slf4j`, `@Data`, etc.) — the root `myapp` module does not.

### Spot-finding algorithm

`ParkingLot.findSpot()` is O(1) regardless of lot size: it iterates at most `|SpotType| × |SpotSize|` = 15 combinations, polling an `ArrayDeque`. Each `VehicleType` encodes a priority-ordered list of preferred `SpotType`s (e.g. `ELECTRIC_CAR` → `[ELECTRIC, COMPACT, GENERAL]`).

### Design patterns implemented

| Pattern | Where |
|---|---|
| Singleton | `ParkingLot` — Spring `@Bean` (inject via DI); static `getInstance()` for non-Spring contexts |
| Builder | `ParkingLot.Builder` — used by `ParkingLotConfig @Bean` |
| Factory | `VehicleFactory`, `ParkingFloorFactory` |
| State | `ParkingSpotState` — `AvailableState`, `OccupiedState`, `OutOfServiceState` |
| Facade | `ParkingFacade` — `ParkingController` talks only to this |

### API endpoints (port 8082)

```
POST   /api/parking/park?type=CAR|MOTORCYCLE|TRUCK|ELECTRIC_CAR&licensePlate=  park vehicle
DELETE /api/parking/exit/{ticketId}   exit and release spot
GET    /api/parking/ticket/{ticketId} lookup active ticket
GET    /api/parking/status            full lot occupancy snapshot
```
