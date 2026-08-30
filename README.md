# Spring DDD Kotlin Example

A **Library** example demonstrating modular DDD with **Kotlin**, **Spring Boot 4.2**, **Spring Modulith 2.2** and **jMolecules**, persisted with **Spring Data JDBC**.

It is the Kotlin port of [spring-ddd-example](https://github.com/hantsy/spring-ddd-example), with three upgrades:

- **Kotlin** (2.4.10, Maven).
- **Spring Data JDBC** instead of JPA, with the domain kept **pure jMolecules** — the **jMolecules ByteBuddy plugin** generates the JDBC mapping (`@Table`, `@Id`, …) at compile time.
- **jMolecules** DDD contracts and **JDBC-backed event publication** (`spring-modulith-starter-jdbc`).

## Architecture

Two bounded contexts (`catalog`, `lending`) plus a shared kernel (`common`):

```
com.example.library
├── catalog
│   ├── domain        Book, Copy (aggregates), BookId/CopyId/Isbn/BarCode (VOs),
│   │                 BookRepository, CopyRepository, BookSearchService (port)
│   ├── application   AddBookToCatalogUseCase, RegisterBookCopyUseCase,
│   │                 DomainEventListener (@ApplicationModuleListener)
│   └── infrastructure OpenLibraryBookSearchService (RestClient), CatalogJdbcConverters
├── lending
│   ├── domain        Loan (aggregate), LoanId/CopyId/UserId (VOs), OverdueFee,
│   │                 LoanCreated/LoanClosed (DomainEvent), LoanRepository
│   ├── application   RentBookUseCase, ReturnBookUseCase, CopyAvailabilityValidator
│   └── infrastructure LendingJdbcConverters
└── common            DomainException, @UseCase stereotype, Clock, logging aspect
```

### DDD contracts (pure jMolecules)

The domain model uses **kmolecules-ddd** interfaces — no Spring Data annotations in the domain:

- Aggregates implement `AggregateRoot<T, ID>` (e.g. `Book : AggregateRoot<Book, BookId>`).
- Identifiers are `data class`es implementing `Identifier` (`BookId`, `CopyId`, `LoanId`).
- Value objects implement `ValueObject` (`Isbn`, `BarCode`) — cross-context references like `lending.CopyId`/`UserId` are annotated `@ValueObject`.
- Within-context references use `Association<T, ID>` (`Copy.bookId : Association<Book, BookId>`).
- Domain events implement `DomainEvent` (`LoanCreated`, `LoanClosed`).

The **jMolecules ByteBuddy plugin** (`transform-extended` + `jmolecules-bytebuddy`) transforms these types at compile time, emitting the Spring Data JDBC mapping (`@Table`, `@Id`, `@Transient`, `MutablePersistable`).

### Persistence (Spring Data JDBC)

- Aggregates map to `book`, `copy`, `loan` tables (`schema.sql`, snake_case naming).
- `Identifier`/`Association` types are converted by `jmolecules-spring`.
- Plain value objects are flattened to single columns via `JdbcCustomConversions`, registered per module in `CatalogJdbcConverters` / `LendingJdbcConverters` and collected by the shared `JdbcConfig`.

### Cross-module events

`Loan` use cases publish `LoanCreated`/`LoanClosed` through `ApplicationEventPublisher`; the catalog's `DomainEventListener` reacts via `@ApplicationModuleListener` (async, after commit) to toggle `Copy.available`. The event publication registry is backed by the JDBC datasource (`spring-modulith-starter-jdbc`, auto-created `event_publication` table).

### Architecture enforcement

- **Spring Modulith** `ApplicationModules.verify()` — module boundaries and the single sanctioned cross-context dependency (`catalog` → `lending :: domain`, declared via `@NamedInterface`).
- **jMolecules** `JMoleculesDddRules.all()` — DDD building-block invariants.

## Build

```bash
./mvnw clean package
```

## Prerequisites

- JDK 25+
- Maven 3.9+ (or the Maven wrapper)
