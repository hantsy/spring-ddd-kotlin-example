package com.example.library.common

/**
 * Base type for all domain exceptions in the Library application.
 *
 * Extending [RuntimeException] means that when a domain exception is thrown
 * from a `@Transactional` use case, Spring rolls back the transaction.
 */
abstract class DomainException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
