# Applying DDD with Spring Boot and Kotlin

This post is for Kotlin developers who want to see how tactical Domain-Driven
Design (DDD) fits into a Spring application. It follows the library sample
from domain objects through persistence, use cases, events, and tests. The
library's requirements and story are covered in the
[Jakarta EE DDD article](https://github.com/hantsy/jakartaee-ddd-example/blob/master/docs/2026-10-08-applying-ddd-with-jakarta-ee-and-jakarta-data.md).

## Strategic design

The design separates Cataloging, which owns books and copies, from Lending,
which owns loans and overdue fees. Lending refers to a copy by its identifier
and publishes loan events so Cataloging can update copy availability without
sharing its entity model. This post focuses on Spring Modulith's
asynchronous, after-commit listeners and the named module interface that make
that boundary explicit; see the linked article for the domain story, context
map, and subdomain rationale.

## Express domain concepts with jMolecules

The domain classes implement jMolecules DDD interfaces instead of depending on
Spring Data annotations. `Book` implements `AggregateRoot<Book, BookId>`, and
`Copy` and `Loan` use the same aggregate-root contract. Identifier and value
types implement `Identifier` or `ValueObject`; loan events implement
`DomainEvent`.

Kotlin data classes make small values concise. For example, `Isbn` validates
its input while keeping a distinct type:

```kotlin
data class Isbn(val value: String) : ValueObject {
    init {
        require(ISBNValidator.getInstance().isValid(value)) {
            "invalid isbn: $value"
        }
    }
}
```

`Copy.bookId` is an `Association<Book, BookId>`, which expresses a reference
to another aggregate by identity rather than embedding a `Book` object.
Lending has its own `CopyId` and doesn't import the catalog's `Copy` type.

## Map the model to Spring Data JDBC

Spring Data JDBC persists the aggregate roots into the `book`, `copy`, and
`loan` tables. The jMolecules ByteBuddy Maven plugin transforms the DDD
contracts into the mapping metadata Spring Data JDBC expects at build time.
The source domain classes therefore stay free of Spring Data mapping
annotations.

`CatalogJdbcConverters` and `LendingJdbcConverters` register converters for
single-column value objects such as `Isbn`, `BarCode`, `CopyId`, and `UserId`.
`JdbcConfig` gathers those converter beans for Spring Data JDBC. The catalog
repository remains an interface:

```kotlin
interface BookRepository : CrudRepository<Book, BookId> {
    fun existsByIsbn(isbn: Isbn): Boolean
}
```

This is not the JPA mapping used in the Java Spring variant. Spring Data JDBC
models persistence around aggregate roots and uses explicit converters for
domain values that don't map directly to scalar columns.

## Coordinate operations with use cases

The Kotlin `@UseCase` annotation composes a Spring service, transaction
boundary, and logging aspect. `AddBookToCatalogUseCase` checks for an existing
ISBN, calls the `BookSearchService` port, and saves a `Book`. `RentBookUseCase`
checks availability, uses the injected `Clock`, saves a `Loan`, and publishes
`LoanCreated`. `ReturnBookUseCase` records the return and any overdue fee
before publishing `LoanClosed`.

The fee schedule stays in the lending domain as `OverdueFee`. `Loan.returned()`
calculates its amount from the return date; the production event for the fee is
left as a code comment and isn't implemented in this example.

## Connect modules with tracked events

The use cases publish events through Spring's `ApplicationEventPublisher`.
Catalog listens with `@ApplicationModuleListener`, which processes the events
asynchronously after the lending transaction commits. The
`spring-modulith-starter-jdbc` dependency provides JDBC-backed event-publication
tracking. Catalog then loads its own `Copy` and updates availability.

`CatalogPackageInfo` and `LendingPackageInfo` declare Modulith module
boundaries. Catalog is allowed to depend on `lending :: domain` for the event
types; lending is allowed to depend on `shared`. These declarations make the
intentional cross-module dependency explicit.

`ApplicationModules.verify()` checks module structure. The Kotlin
`ArchitectureTest` also runs `JMoleculesDddRules.all()` to validate the
declared DDD building blocks. Module-scoped tests use Spring Modulith's
`Scenario` API to observe event publication and catalog state changes.

## Compare what the tests prove

`LibraryIntegrationTest` exercises sequential rent and return behavior,
availability updates, a second-rental rejection, and the overdue fee. The
rejection assertion accepts `Exception`, so it doesn't prove the precise
exception type. The test waits for asynchronous availability updates, but it
doesn't exercise simultaneous rentals.

The schema and H2 test setup are for this example. The DDD interfaces and
converter setup illustrate one way to keep the Kotlin domain separate from
Spring Data JDBC mapping; they don't remove the need to test database
constraints and transaction behavior for a production deployment.

## Run the sample

The project targets Java 25 and builds with its Maven wrapper:

```shell
./mvnw clean package
```

The network-dependent Open Library test is tagged `integration` and excluded
from the default build. Run it separately with:

```shell
./mvnw test -Dgroups=integration
```

## Further reading

- [Spring Modulith reference](https://docs.spring.io/spring-modulith/reference/)
- [jMolecules documentation](https://jmolecules.org/)
- [Jakarta EE DDD article](https://github.com/hantsy/jakartaee-ddd-example/blob/master/docs/2026-10-08-applying-ddd-with-jakarta-ee-and-jakarta-data.md)
- [Spring Java DDD example](https://github.com/hantsy/spring-ddd-example)

The [Helidon](https://github.com/hantsy/helidon-ddd-example) and
[Quarkus](https://github.com/hantsy/quarkus-ddd-example) repositories
implement the same example with different persistence and event stacks.
