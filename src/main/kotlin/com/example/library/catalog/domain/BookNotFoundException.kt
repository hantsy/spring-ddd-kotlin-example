package com.example.library.catalog.domain

import com.example.library.common.DomainException

/**
 * Thrown when a book cannot be found, e.g. a book search by ISBN returned
 * no result or a repository lookup by id came up empty.
 */
class BookNotFoundException(isbn: Isbn) : DomainException("book not found for isbn: ${isbn.value}")
