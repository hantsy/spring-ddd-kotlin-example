# Applying DDD with Spring Boot and Kotlin

Kotlin makes it natural to model small domain values as types, but persistence
frameworks still need to understand how those types fit into an aggregate.
This version uses jMolecules to declare the DDD model, its Byte Buddy plugin
to produce Spring Data JDBC mapping metadata, and Spring Modulith to guard
module boundaries. This post follows the Kotlin implementation from model to
persistence.

## Strategic design

Cataloging owns books and copies; Lending owns loans and fees. A copy
association stays an identifier across the context boundary, and loan events
carry the facts Cataloging needs to update availability. For the requirements
and context map behind this arrangement, take a look at the
[Jakarta EE DDD article](https://github.com/hantsy/jakartaee-ddd-example/blob/master/docs/2026-10-08-applying-ddd-with-jakarta-ee-and-jakarta-data.md).
This Kotlin example picks up from there and shows how those decisions become
typed model contracts and a narrow module API.

## Part II — Tactical design with Spring Boot and Kotlin

The key question in this variant is how to keep a DDD-shaped Kotlin model
without shaping it around persistence annotations. jMolecules describes the
building blocks, a build-time transformation supplies Spring Data JDBC's
mapping metadata, and Spring Modulith keeps the contexts' dependencies
explicit.

### 1. Entities, value objects, and aggregate roots

#### Which concepts are entities and which are values

`Book`, `Copy`, and `Loan` have identity and a lifecycle, so the model treats
them as entities and aggregate roots. The Kotlin classes implement
jMolecules' `AggregateRoot<T, ID>` interface: `Book` and `Copy` belong to
Cataloging, and `Loan` belongs to Lending. This contract makes the root type
and its identifier explicit to readers and to tools that inspect the model.

jMolecules offers both annotations and type interfaces for describing DDD
building blocks. This code uses interfaces: for example,
`Book : AggregateRoot<Book, BookId>` rather than the commented alternative
`@AggregateRoot` annotation. Cataloging's `BookId` and `CopyId`, and
Lending's `LoanId`, implement `Identifier`. `Isbn` and `BarCode` implement
`ValueObject`; Lending's `CopyId` and `UserId` also implement `ValueObject`
because those types represent references it consumes, not identities it
creates. `Copy.bookId` uses `Association<Book, BookId>` to express a typed
reference to another aggregate by ID. `LoanCreated` and `LoanClosed`
implement jMolecules' `DomainEvent` interface. `Isbn` validates its value at
construction. `OverdueFee` and `BookInformation` are domain types but do not
declare jMolecules marker interfaces in this project.

These contracts improve the model in two ways: they document intent in the
type system, and they let jMolecules integrations validate and process the
model without asking Spring Data annotations to define its domain meaning.
The interfaces are metadata contracts, not persistence mappings or automatic
invariant enforcement.

The same model metadata also participates in persistence integration. The
project combines `jmolecules-spring` with Spring Data JDBC: the integration
bridges jMolecules `Identifier` and `Association` types to Spring's conversion
system. The project provides explicit converters for values such as `Isbn`
and `BarCode`. `JdbcConfig` collects those converter beans for Spring Data
JDBC.

Spring Data JDBC also needs mapping metadata for aggregate roots. Rather than
adding Spring Data annotations to the domain classes, the Maven build runs the
Byte Buddy `transform-extended` goal with `jmolecules-bytebuddy`. At compile
time, this integration transforms the jMolecules-described classes with the
Spring Data JDBC mapping shape they need, including table, ID, transient, and
persistable metadata. The resulting classes can be mapped by Spring Data JDBC
while their source remains expressed in DDD terms.

#### Aggregate boundaries

`Copy` refers to its book through `Association<Book, BookId>`. `Loan` stores
the Lending context's `CopyId` and `UserId`. Neither aggregate embeds another
aggregate.

> Aggregates refer to each other by identity, not by containment.

#### Invariants and their guards

`Copy` changes availability through `makeAvailable()` and
`makeUnavailable()`. `Loan.returned()` records the return and calculates the
applicable overdue fee. `CopyAvailabilityValidator` checks the open-loan rule
through `LoanRepository`.

`RegisterBookCopyUseCase` accepts a `BookId` but does not check that the book
exists before saving the copy. The sample therefore does not enforce the
book-to-copy association invariant.

The check supports sequential requests. The integration tests do not exercise
simultaneous rentals, so they do not prove that the availability rule is
race-free.

#### Map the model with jMolecules and Spring Data JDBC

jMolecules declares the DDD role of each type; it does not supply Spring Data
mapping annotations. `Book` declares its aggregate root and identifier
through jMolecules:

```kotlin
class Book(
    override val id: BookId,
    val title: String,
    val isbn: Isbn,
) : AggregateRoot<Book, BookId> {
    constructor(title: String, isbn: Isbn) :
        this(BookId.newInstance(), title, isbn)
}
```

The domain classes do not carry Spring Data JDBC annotations. During the
Maven build, the `jmolecules-bytebuddy` plugin transforms the jMolecules
metadata into mapping metadata Spring Data JDBC can use.
`CatalogJdbcConverters` and `LendingJdbcConverters` convert value types stored
in single columns.

### 2. Repositories with Spring Data JDBC

The aggregate repositories extend Spring Data's `CrudRepository` and are
declared beside their domain types:

```kotlin
interface BookRepository : CrudRepository<Book, BookId> {
    fun existsByIsbn(isbn: Isbn): Boolean
}
```

Spring Data JDBC persists the `Book`, `Copy`, and `Loan` aggregate roots. The
repository interfaces provide persistence contracts; Spring Data supplies
their implementations. `jmolecules-spring` handles conversions for
jMolecules `Identifier` and `Association` types. The
`CatalogJdbcConverters` and `LendingJdbcConverters` beans register explicit
converters that flatten application value types such as `Isbn` and `BarCode`
to single columns; `JdbcConfig` collects those converters for Spring Data JDBC.

This is aggregate-oriented Spring Data JDBC mapping, not the JPA mapping in
the Java Spring variant. The project keeps conversion at the infrastructure
boundary instead of making the domain types depend on Spring persistence
annotations.

### 3. Domain events with Spring Modulith

#### Events in this example

Lending publishes `LoanCreated` and `LoanClosed` through Spring's
`ApplicationEventPublisher`. Each event carries a Lending `CopyId`, not a
Cataloging `Copy`. The event types implement jMolecules' `DomainEvent`,
marking their domain role in addition to serving as Spring event payloads.
The loan records overdue fees, but the sample does not publish an
`OverdueFeeCalculated` event.

#### Publish and observe Spring Modulith events

Cataloging handles them in `DomainEventListener` methods annotated with
`@ApplicationModuleListener`. Spring Modulith processes the listeners
asynchronously after the Lending transaction commits. The listener translates
the identifier, loads Cataloging's `Copy`, and updates availability. The
`spring-modulith-starter-jdbc` dependency tracks event publications with JDBC.

The event registry tracks listener processing inside this modular monolith.
It does not make the event publisher a broker or the two contexts independent
services.

### 4. Domain and application services with Spring

Application services coordinate use cases; domain services enforce rules that
depend on more than one aggregate. Spring provides injection and transaction
management.

#### 4.1 Application services and transactions

`AddBookToCatalogUseCase`, `RegisterBookCopyUseCase`, `RentBookUseCase`, and
`ReturnBookUseCase` coordinate application operations. The shared
`@UseCase` annotation composes Spring's `@Service`, `@Transactional`, and the
sample's `@Logged` aspect.

`RentBookUseCase` checks availability, obtains the current time from an
injected `Clock`, saves a `Loan`, and publishes `LoanCreated`.
`ReturnBookUseCase` records the return and fee on the `Loan`, saves the
aggregate, and publishes `LoanClosed`.

#### 4.2 Domain services and cross-aggregate rules

`CopyAvailabilityValidator` uses `LoanRepository` to check for an open loan
for a copy. It raises `CopyNotAvailableException` when the copy is unavailable.
The validator expresses a rule across `Loan` instances, so it is separate from
the state of one loan.

The validator is in the domain package but is a Spring component. The
architecture tests check the declared DDD building blocks and Spring Modulith
modules; they do not add a custom ArchUnit rule to ban this Spring dependency.

#### 4.3 Ports and adapters with Spring

Cataloging declares `BookSearchService` as the port for ISBN lookup.
`OpenLibraryBookSearchService` implements the port in the infrastructure
package and translates provider data into `BookInformation`. The catalog use
case depends on the domain contract, not on HTTP or the external response
shape.

### 5. Package and module boundary verification

The package structure gives Spring Modulith module boundaries to `catalog`,
`lending`, and `shared`. Each business context has application and domain
packages; infrastructure packages hold persistence converters and the
external book-search adapter. The `lending.domain` package is a named module
interface so Cataloging can use the loan-event types without importing the
rest of Lending's internals.

#### Check DDD building blocks with ArchUnit

`ArchitectureTest` uses ArchUnit to run `JMoleculesDddRules.all()` over the
application classes. Those rules check the structural use of jMolecules
building blocks, including aggregate roots, identifiers, value objects, and
domain events. They do not define custom dependency-direction rules between
the application's `application`, `domain`, and `infrastructure` packages.

#### Verify module dependencies with Spring Modulith

Spring Modulith checks module dependencies separately from the DDD model
rules. The declarations are regular Kotlin source files annotated with
`@PackageInfo`, because Kotlin does not use Java's `package-info.java`
mechanism. The catalog module's `CatalogPackageInfo.kt`, located at
`src/main/kotlin/com/example/library/catalog/CatalogPackageInfo.kt`, allows
only `lending :: domain`:

```kotlin
@PackageInfo
@ApplicationModule(allowedDependencies = ["lending :: domain"])
class CatalogPackageInfo
```

The matching `@NamedInterface("domain")` declaration is in
`src/main/kotlin/com/example/library/lending/domain/LendingDomainPackageInfo.kt`.
`src/main/kotlin/com/example/library/lending/LendingPackageInfo.kt` limits
Lending's dependency to `shared`. `ModularityTest` calls
`ApplicationModules.verify()` to detect disallowed module dependencies and
cycles. These concrete package declarations make the cross-context contract
executable rather than a package-naming convention.

Together, ArchUnit and Spring Modulith check different boundaries:
jMolecules' DDD building-block rules validate the model's declared roles,
while Modulith validates which modules may depend on one another. Neither
test currently defines the custom layer-direction rules used by the Java
Spring and Quarkus examples.

`CatalogModuleTest` uses Modulith's `Scenario` API to verify event-driven
behavior.

## Part III — Verify scenarios with integration tests

`LibraryIntegrationTest` exercises sequential rent and return behavior, copy
availability, rejection of a second rental, and overdue-fee calculation. The
second-rental assertion accepts `Exception`, so it does not verify the exact
domain exception type. Module-scoped tests verify the asynchronous catalog
reaction.

The tests do not exercise concurrent rentals. They therefore do not establish
that the availability check is race-free.

## Run the sample

Build the project and run the default tests with:

```shell
./mvnw clean package
```

To run the network-dependent Open Library integration test separately, use:

```shell
./mvnw test -Dgroups=integration
```

## Summary

The Kotlin variant keeps DDD intent in the source model: jMolecules contracts
describe the aggregates and relationships, while its Byte Buddy integration
and explicit converters make that model persistable with Spring Data JDBC.
Spring Modulith separately verifies the module boundary and manages
after-commit event handling.

### Further reading

- [Spring Modulith reference](https://docs.spring.io/spring-modulith/reference/)
- [jMolecules documentation](https://jmolecules.org/)
- [Spring Boot Java DDD example](https://github.com/hantsy/spring-ddd-example)
- [Helidon DDD example](https://github.com/hantsy/helidon-ddd-example)
- [Quarkus DDD example](https://github.com/hantsy/quarkus-ddd-example)
