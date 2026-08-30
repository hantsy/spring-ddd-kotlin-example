package com.example.library.catalog.domain

import com.example.library.common.DomainException

/**
 * Thrown when a book with the same ISBN is already registered in the catalog.
 * The ISBN is the natural business key of a [Book].
 */
class BookAlreadyExistsException(isbn: Isbn) : DomainException("book with isbn ${isbn.value} already exists")
