# Java & Spring Boot Interview Prep Skill

You are a Java expert helping with production-quality Spring Boot development and interview preparation. The user is building hands-on projects at `/Users/oceanblue/dev/myapp` to prepare for senior Java interviews. Use the concepts and examples below as your reference for any Java, Spring Boot, concurrency, streams, or OOP question — grounded in the actual code in this repo.

**When answering: be concise, use code from this codebase as examples, and always explain the WHY not just the what.**

---

## Java Streams

### Operations quick reference

| Operation | Type | Input → Output | Use when |
|---|---|---|---|
| `map(f)` | Intermediate | `Stream<A>` → `Stream<B>` | Transform each element, one-to-one |
| `flatMap(f)` | Intermediate | `Stream<A>` → `Stream<B>` | Each element produces a stream; flatten all into one |
| `filter(pred)` | Intermediate | `Stream<A>` → `Stream<A>` | Keep elements matching a condition |
| `sorted(comp)` | Intermediate | `Stream<A>` → `Stream<A>` | Sort elements |
| `distinct()` | Intermediate | `Stream<A>` → `Stream<A>` | Remove duplicates |
| `reduce(id, fn)` | Terminal | `Stream<A>` → `A` | Collapse all elements into one value |
| `collect(c)` | Terminal | `Stream<A>` → collection | Gather into List, Map, Set |
| `forEach(f)` | Terminal | `Stream<A>` → void | Side effects only |
| `count()` | Terminal | `Stream<A>` → `long` | Count elements |
| `findFirst()` | Terminal | `Stream<A>` → `Optional<A>` | First match |

### map vs flatMap
```java
// map — one element in, one element out
list.stream().map(String::toUpperCase)   // Stream<String> → Stream<String>

// map producing a stream — gives you nested streams (usually wrong)
drivers.stream()
    .map(d -> d.getDeliveries().stream())  // Stream<Stream<DeliveryRecord>> ← can't reduce

// flatMap — one element in, many elements out, all flattened
drivers.stream()
    .flatMap(d -> d.getDeliveries().stream())  // Stream<DeliveryRecord> ← ready to use
```

### reduce
```java
.reduce(BigDecimal.ZERO, BigDecimal::add)
//      ↑ identity         ↑ combiner: (acc, next) -> acc.add(next)
// acc=0 → acc=15.00 → acc=23.50 → acc=35.50 → returns 35.50
```

### Method references
```java
record -> record.getCost()     // lambda
DeliveryRecord::getCost        // method reference — preferred, reads like English

(acc, next) -> acc.add(next)   // lambda
BigDecimal::add                // method reference
```

### Streams and concurrency
- `.stream()` = sequential, one thread → always safe
- `.parallelStream()` = splits across thread pool → safe only if lambdas are **stateless** (read inputs, return result, never mutate shared state)
- `forEach` with side effects in parallel stream = race condition

### Intermediate vs terminal
Stream pipeline is **lazy** — nothing executes until a terminal operation is called. Intermediate operations just build the pipeline description.

---

## Concurrency & Multithreading

### The root problem
Heap memory is **shared** across all threads. Stack memory is **per-thread**. Only heap data needs protection.

```
Thread A stack    Thread B stack       Heap (shared)
─────────────     ─────────────        ─────────────
timestamp = 3600  timestamp = 7200     ConcurrentHashMap<driverId, Driver>
oldWatermark = 0  oldWatermark = 0     Driver { paidUpTo: AtomicLong }
                                       DeliveryRecord (immutable)
```

### Three roads to thread safety

**1. Immutability — nothing can change, so nothing can race**
```java
@Value                          // Lombok — all fields final, no setters
public class DeliveryRecord { ... }

private final BigDecimal hourlyRate;   // final field — set once, never changed
```
Immutable objects can be shared freely across any number of threads.

**2. Atomic types — controlled mutation in one CPU instruction**
```java
AtomicLong paidUpTo = new AtomicLong(0);

// WRONG — three separate steps, thread can sneak between any two:
long old = paidUpTo.get();           // step 1: read
long newVal = Math.max(old, ts);     // step 2: compute
paidUpTo.set(newVal);                // step 3: write

// RIGHT — one indivisible instruction:
long old = paidUpTo.getAndUpdate(cur -> Math.max(cur, ts));
```

Key `AtomicLong` methods:
- `get()` — read current value
- `set(v)` — write value
- `getAndUpdate(fn)` — atomically: return old value, set new value from fn
- `updateAndGet(fn)` — atomically: set new value from fn, return new value
- `compareAndSet(expect, update)` — set only if current == expect

**3. Concurrent collections — thread-safe data structures**

| Need | Use | Not |
|---|---|---|
| Thread-safe map | `ConcurrentHashMap` | `HashMap` |
| Thread-safe list, read-heavy | `CopyOnWriteArrayList` | `ArrayList` |
| Thread-safe queue | `ConcurrentLinkedQueue` | `LinkedList` |
| Thread-safe counter | `AtomicLong` / `AtomicInteger` | `long` / `int` |

### CopyOnWriteArrayList
Every write (`add`, `remove`) creates a **full copy** of the underlying array. Reads (`.stream()`, `get()`) operate on a **snapshot** — never block, never throw `ConcurrentModificationException`.

```java
// Two threads simultaneously:
Thread A: list.stream()...    // snapshot taken at this moment
Thread B: list.add(record)    // writes to a NEW copy, doesn't affect A's snapshot
```
Best for: **read-heavy, write-rare** lists. Bad for write-heavy (full copy on every write).

### ConcurrentHashMap
Segments the map into stripes — concurrent reads never block, writes only lock one stripe at a time. `putIfAbsent` is atomic — safe for "register if not exists" pattern:
```java
Driver existing = drivers.putIfAbsent(driverId, new Driver(driverId, rate));
if (existing != null) throw new DriverAlreadyExistsException(driverId);
```

### Stack vs Heap

| | Stack | Heap |
|---|---|---|
| What lives here | Local vars, method params, references (pointers) | Objects (`new`), instance fields, static fields, String pool |
| Per thread? | Yes — each thread has its own | No — shared across all threads |
| Thread safe? | Always | Only if immutable or properly synchronized |
| Lifespan | Method call to return | Until GC |

```java
public BigDecimal payUpTo(long timestamp) {   // timestamp → stack
    long oldWatermark = ...get();             // oldWatermark → stack
    BigDecimal result = new BigDecimal(...);  // reference → stack; object → heap
}   // stack frame destroyed on return
```

### Object escape
A locally-created object is thread-safe until another thread can reach it:
```java
DeliveryRecord record = new DeliveryRecord(...);  // heap, but only this thread has a reference
driver.addDelivery(record);                        // now shared — other threads can reach it
// Safe only because DeliveryRecord is immutable (@Value)
```

### synchronized vs Atomic vs concurrent collections
| Tool | When |
|---|---|
| `AtomicLong/Boolean/Reference` | Single variable, simple read-modify-write |
| `ConcurrentHashMap` | Shared map |
| `CopyOnWriteArrayList` | Shared read-heavy list |
| `synchronized` block | Multi-step sequence that must be atomic as a whole |
| `ReentrantLock` | Need tryLock, timeout, or fairness |

---

## BigDecimal

### Why not double for money?
```java
System.out.println(0.1 + 0.2);  // 0.30000000000000004 — binary float error
```
`BigDecimal` uses exact decimal arithmetic — no rounding surprises.

### Key methods
```java
BigDecimal a = new BigDecimal("10.00");        // always use String constructor for literals
BigDecimal b = BigDecimal.valueOf(3600);        // safe for long/double

a.add(b)                                        // a + b → new BigDecimal
a.multiply(b)                                   // a × b → new BigDecimal
a.divide(b, 2, RoundingMode.HALF_UP)            // a / b, 2 decimal places, round half-up
a.compareTo(b)                                  // -1, 0, 1 — use instead of equals for value comparison
```

### Immutability
Every operation returns a **new** `BigDecimal` — the original is never modified. Makes it thread-safe by default.

### RoundingMode
- `HALF_UP` — standard financial rounding (1.005 → 1.01). Use for money.
- `HALF_EVEN` — banker's rounding, reduces cumulative error over many operations
- `FLOOR` / `CEILING` — always round down / up

---

## Optional

```java
// Instead of null checks:
if (driver != null) { return driver.getHourlyRate(); }

// Use Optional:
Optional.ofNullable(drivers.get(id))
    .map(Driver::getHourlyRate)
    .orElseThrow(() -> new DriverNotFoundException(id));
```

Key methods:
- `.map(fn)` — transform value if present
- `.orElse(default)` — return default if empty
- `.orElseGet(supplier)` — lazy default (computed only if needed)
- `.orElseThrow(supplier)` — throw if empty
- `.filter(pred)` — empty if predicate fails
- `.ifPresent(fn)` — side effect if present

---

## Java 8+ Features

### Lambdas & @FunctionalInterface
```java
@FunctionalInterface
interface BookSortStrategy {
    List<Book> sort(List<Book> books);
}

// Callers pass inline lambdas instead of anonymous classes:
BookSortStrategy byTitle = books -> books.stream()
    .sorted(Comparator.comparing(Book::getTitle)).toList();
```

### Switch expressions (Java 14+)
```java
// Old:
String result;
switch (format) {
    case "csv": result = "..."; break;
    default: result = "...";
}

// New — expression form, no fall-through, no break:
String result = switch (format) {
    case "csv"      -> generateCsv();
    case "summary"  -> generateSummary();
    default         -> throw new IllegalArgumentException(format);
};
```

### Records (Java 16+)
```java
public record BookResponse(Long id, String title, String author) {}
// Auto-generates: constructor, getters, equals, hashCode, toString
// Immutable by default — all fields final
```

### Comparator composition
```java
Comparator.comparing(Book::getTitle)
    .thenComparing(Book::getAuthor)
    .reversed()
```

---

## Spring Boot Patterns

### Layered architecture (used in all modules)
```
Controller → Facade → Service → Repository
```
- Controller: HTTP concerns only, delegates to Facade
- Facade: single entry point into service layer, orchestrates
- Service: business logic
- Repository: data access

### Dependency injection
```java
@RequiredArgsConstructor          // Lombok — generates constructor for all final fields
public class DeliveryFacade {
    private final IDeliveryService deliveryService;  // injected by Spring
}
```
Program to interfaces (`IDeliveryService`), not implementations — enables swapping, mocking in tests.

### @Profile for swappable implementations
```java
@Profile("memory")   class InMemoryBookRepository  implements BookRepository {}
@Profile("postgres") class PostgresBookRepository  implements BookRepository {}
// Switch with: spring.profiles.active=memory
```

### Bean Validation
```java
@NotBlank String driverId;
@DecimalMin("0.01") BigDecimal hourlyRate;
// Activated by @Valid on the controller parameter
```

### Exception handling
```java
@RestControllerAdvice
public class DeliveryExceptionHandler {
    @ExceptionHandler(DriverNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(DriverNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}
```

---

## Lombok Quick Reference

| Annotation | What it generates |
|---|---|
| `@Getter` | Getters for all fields |
| `@Setter` | Setters for all fields |
| `@Value` | All fields final + `@Getter @AllArgsConstructor @EqualsAndHashCode @ToString` |
| `@Data` | `@Getter @Setter @RequiredArgsConstructor @EqualsAndHashCode @ToString` |
| `@Builder` | Builder inner class |
| `@RequiredArgsConstructor` | Constructor for all `final` / `@NonNull` fields |
| `@Slf4j` | `private static final Logger log = LoggerFactory.getLogger(...)` |

---

## Projects in This Repo

### `myapp` (port 8080) — Library Management
Full GoF pattern showcase: Singleton, Builder, Proxy, Facade, State, Command, Observer, Chain of Responsibility, Strategy, Specification, Decorator, Adapter, Template Method, Abstract Factory, Mediator, AOP.
Reference: `/rippling-delivery` for delivery system; use design-patterns skill for library patterns.

### `myapp-p3` (port 8082) — Parking Lot System
OOP interview problem. Patterns: Singleton, Builder, Factory, State, Facade.
In-memory: `ConcurrentHashMap` for tickets, `EnumMap<SpotType, EnumMap<SpotSize, Deque<ParkingSpot>>>` for O(1) spot finding.

### `myapp-p1` (port 8083) — Delivery Driver Payment System
Rippling interview practice. See dedicated section below.

### `myapp-p2` (port 8084) — Expense Rule Engine
Rippling interview practice. See dedicated section below.

---

## Delivery Driver Payment System — `myapp-p1`

### Domain model
```java
@Value                              // immutable value object
public class DeliveryRecord {
    long startTime;                 // epoch seconds, inclusive
    long endTime;                   // epoch seconds, exclusive
    BigDecimal cost;                // pre-computed at recordDelivery time
}

@Getter
public class Driver {
    private final String driverId;
    private final BigDecimal hourlyRate;
    private final List<DeliveryRecord> deliveries = new CopyOnWriteArrayList<>();
    private final AtomicLong paidUpTo = new AtomicLong(0);
}
```

### Part 1 — Cost formula
```java
// cost = hourlyRate × (endTime - startTime) / 3600
BigDecimal cost = driver.getHourlyRate()
    .multiply(BigDecimal.valueOf(endTime - startTime))
    .divide(SECONDS_PER_HOUR, 2, RoundingMode.HALF_UP);
```
Each delivery is independent — no merging for overlapping deliveries. Count is never a multiplier.

### Part 2 — Payment watermark
```java
// Atomically capture old value and advance watermark in one step:
long oldWatermark = driver.getPaidUpTo()
    .getAndUpdate(cur -> Math.max(cur, timestamp));

// Core slice cost helper used by both newlyPaidCost and unpaidCost:
private BigDecimal costForSlice(Driver driver, long sliceStart, long sliceEnd) {
    if (sliceStart >= sliceEnd) return BigDecimal.ZERO;
    return driver.getHourlyRate()
        .multiply(BigDecimal.valueOf(sliceEnd - sliceStart))
        .divide(SECONDS_PER_HOUR, 2, RoundingMode.HALF_UP);
}
```

### Straddling delivery
```
Delivery: start=1800, end=9000, rate=$10/hr
payUpTo(3600):
  newly paid = costForSlice(1800, 3600) = $5.00
  remaining  = costForSlice(3600, 9000) = $15.00  ← getTotalUnpaid()
```

### API
```
POST  /api/delivery/drivers                        registerDriver
POST  /api/delivery/drivers/{id}/deliveries        recordDelivery
GET   /api/delivery/drivers/{id}/cost              getTotalCost
POST  /api/delivery/payments?upTo={ts}             payUpTo → { paidUpTo, amountPaid }
GET   /api/delivery/payments/unpaid                getTotalUnpaid
```

### Interview follow-ups
- **Out-of-order delivery?** `max(startTime, paidUpTo)` handles it — pre-watermark slice returns ZERO automatically
- **Concurrent payUpTo?** `getAndUpdate` is atomic — each thread gets unique `oldWatermark`, no double-counting
- **Scale to 1M drivers?** Shard `ConcurrentHashMap` by `driverId` hash across nodes
- **Persistence?** Persist `paidUpTo` watermark + delivery records to DB; reload watermarks on startup

---

## Expense Rule Engine — `myapp-p2`

### Domain model
```java
@Value @Builder
public class Expense {
    String id, employeeId, tripId;
    BigDecimal amount;
    ExpenseCategory category;   // TRAVEL | MEALS | SOFTWARE | OTHER
    String merchant;
    LocalDate date;
}
```

### Rule hierarchy
```
Rule (interface — single Expense)        TripRule (interface — List<Expense>)
├── ExpenseRule                          └── ExpenseTripRule
├── AndRule      ← Composite pattern
├── OrRule
└── NotRule
```
`Rule` and `TripRule` are **separate interfaces** — composite rules only apply to per-expense evaluation.

### Part 1 — Bug fix (string comparison)
Amounts arrive as strings. String comparison gives wrong results:
```
"9" > "153" → true (string: "9" > "1" alphabetically)   WRONG
  9 > 153   → false (BigDecimal)                          CORRECT
```
Fix is in `RuleOperator` enum — each operator carries its own evaluation logic:
```java
GREATER_THAN {
    public boolean evaluate(String fieldValue, String ruleValue) {
        try { return new BigDecimal(fieldValue).compareTo(new BigDecimal(ruleValue)) > 0; }
        catch (NumberFormatException e) { throw new IllegalArgumentException("GREATER_THAN requires numeric values..."); }
    }
}
EQUALS {
    public boolean evaluate(String fieldValue, String ruleValue) {
        try { return new BigDecimal(fieldValue).compareTo(new BigDecimal(ruleValue)) == 0; }
        catch (NumberFormatException e) { return fieldValue.equalsIgnoreCase(ruleValue); }
        // EQUALS falls back to string — valid for CATEGORY/MERCHANT. GREATER_THAN/LESS_THAN fail loud.
    }
}
```

### Part 2 — Per-expense rules
```java
// RuleField enum — each constant knows how to extract its value from an Expense
AMOUNT   { public String extract(Expense e) { return e.getAmount().toPlainString(); } }
CATEGORY { public String extract(Expense e) { return e.getCategory().name(); } }
MERCHANT { public String extract(Expense e) { return e.getMerchant(); } }

// ExpenseRule — delegates to field then operator
public boolean isViolated(Expense expense) {
    return operator.evaluate(field.extract(expense), value);
}

// Service evaluation pipeline
expenses.stream()
    .filter(expense -> rules.stream().anyMatch(rule -> rule.isViolated(expense)))
    .toList();
```
Adding a new field = add one enum case to `RuleField`. Adding a new operator = add one enum case to `RuleOperator`. No if/else chains anywhere.

### Part 3 — Trip-level aggregation rules
```java
// TripRuleField — aggregates over a list of expenses
TOTAL_AMOUNT { public String extract(List<Expense> expenses) {
    return expenses.stream().map(Expense::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString(); } }
EXPENSE_COUNT { public String extract(List<Expense> expenses) {
    return String.valueOf(expenses.size()); } }

// evaluateTrips — group → filter violating trips → collect
expenses.stream()
    .collect(Collectors.groupingBy(Expense::getTripId))          // Map<tripId, List<Expense>>
    .entrySet().stream()
    .filter(entry -> tripRules.stream().anyMatch(rule -> rule.isViolated(entry.getValue())))
    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
```
`RuleOperator` is reused — `TripRuleField.extract()` converts the aggregated value to a String, then the same operator comparison logic applies.

### Part 4 — Composite AND / OR / NOT rules (Composite pattern)
```java
// Three classes, all implement Rule — service needs zero changes
AndRule  → rules.stream().allMatch(rule -> rule.isViolated(expense))
OrRule   → rules.stream().anyMatch(rule -> rule.isViolated(expense))
NotRule  → !rule.isViolated(expense)

// buildRule in facade — recursive switch expression
private Rule buildRule(CompositeRuleRequest request) {
    return switch (request.getType().toUpperCase()) {
        case "SIMPLE" -> new ExpenseRule(field, operator, value);
        case "AND"    -> new AndRule(request.getRules().stream().map(this::buildRule).toList());
        case "OR"     -> new OrRule(request.getRules().stream().map(this::buildRule).toList());
        case "NOT"    -> new NotRule(buildRule(request.getRule()));
        default       -> throw new IllegalArgumentException("Unknown rule type: " + request.getType());
    };
}
// this::buildRule recurses — stops when it hits SIMPLE
```
JSON request is a recursive DTO (`CompositeRuleRequest`) with a `type` field and nullable `rules`/`rule` fields.

### Reimbursement calculation (budget per category per trip)
```java
// Budget stored as ConcurrentHashMap<ExpenseCategory, BigDecimal> — O(1) lookup
// calculateReimbursement():
//   1. Group expenses by tripId
//   2. Per trip: group by category, look up budget rule
//   3. applyBudget() — sequential loop tracks remaining budget

private void applyBudget(List<Expense> expenses, BigDecimal limit, List<ExpenseReimbursement> details) {
    BigDecimal remaining = limit;
    for (Expense expense : expenses) {
        if (remaining <= 0)                          // budget gone → fully rejected
        else if (expense.amount <= remaining)        // fits → fully approved, reduce remaining
        else {                                       // overlapping — the key case
            approved = remaining;
            rejected = expense.amount - remaining;
            remaining = ZERO;
        }
    }
}
```
Why a loop not a stream: `remaining` is mutable state that each expense reads and updates sequentially. A stream would require a mutable wrapper hack — the loop is clearer.

Three cases tested:
```
e1=$100, e2=$50, budget=$120:
  e1: 100 ≤ 120  → approved=$100, remaining=$20
  e2: 50 > 20    → approved=$20,  rejected=$30  ← the overlapping case
```

### API endpoints
```
POST  /api/expenses                     addExpense
POST  /api/expenses/rules               addRule (simple per-expense)
POST  /api/expenses/rules/composite     addCompositeRule (AND/OR/NOT)
POST  /api/expenses/trip-rules          addTripRule
POST  /api/expenses/budget-rules        addBudgetRule (category + limit)
GET   /api/expenses/evaluate            evaluateExpenses → violations list
GET   /api/expenses/evaluate/trips      evaluateTrips → tripId → expenses map
GET   /api/expenses/reimbursement       calculateReimbursement → tripId → approved/rejected
```

### Interview follow-ups
- **CONTAINS on AMOUNT?** Valid concern — nothing stops it. Fix: validate field+operator combo in facade before storing.
- **EQUALS with "500.00" vs "500"?** Fixed — try BigDecimal first, fall back to string for non-numeric fields.
- **Why separate Rule and TripRule interfaces?** Different signatures — `Rule.isViolated(Expense)` vs `TripRule.isViolated(List<Expense>)`. Can't share without generics.
- **Composite trip rules?** Would need `AndTripRule`, `OrTripRule` implementing `TripRule` — same pattern, different interface.
- **Scale?** Pre-index expenses by field value so you don't scan all N expenses per rule. Evaluate cheap rules first, short-circuit.
