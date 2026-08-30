# Spring DDD Kotlin Example

A **Library** example demonstrating modular DDD with **Kotlin**, **Spring Boot 4.2**, **Spring Modulith 2.2** and **jMolecules**, using **Spring Data JDBC** for persistence.

It is the Kotlin port of [spring-ddd-example](https://github.com/hantsy/spring-ddd-example), with three upgrades:

- **Kotlin** (2.4.10, Maven).
- **Spring Data JDBC** instead of JPA, with the domain kept **pure jMolecules** — the **jMolecules ByteBuddy plugin** generates the JDBC mapping (`@Table`, `@Id`, …) at compile time.
- **jMolecules** DDD contracts (`AggregateRoot`/`Identifier`/`ValueObject`, `@DomainEvent`, layered architecture) and **JDBC-backed event publication** (`spring-modulith-starter-jdbc`).

## Architecture

Two bounded contexts (`catalog`, `lending`) + shared kernel (`common`), verified by Spring Modulith's verifier and jMolecules ArchUnit rules.

## Build

```bash
./mvnw clean package
```

## Prerequisites

- JDK 25+
- Maven 3.9+ (or the Maven wrapper)
