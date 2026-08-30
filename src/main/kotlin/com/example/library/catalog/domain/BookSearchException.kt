package com.example.library.catalog.domain

import com.example.library.common.DomainException

/**
 * Thrown when an external book search fails for reasons other than "not found",
 * such as an unexpected upstream status code or a network error.
 */
class BookSearchException(message: String, cause: Throwable? = null) : DomainException(message, cause)
