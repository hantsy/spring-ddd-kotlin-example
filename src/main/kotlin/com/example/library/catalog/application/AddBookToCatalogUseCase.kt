package com.example.library.catalog.application

import com.example.library.catalog.domain.Book
import com.example.library.catalog.domain.BookAlreadyExistsException
import com.example.library.catalog.domain.BookRepository
import com.example.library.catalog.domain.BookSearchService
import com.example.library.catalog.domain.Isbn
import com.example.library.common.UseCase

@UseCase
class AddBookToCatalogUseCase(
    private val bookSearchService: BookSearchService,
    private val bookRepository: BookRepository,
) {
    fun execute(isbn: Isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw BookAlreadyExistsException(isbn)
        }
        val result = bookSearchService.search(isbn)
        bookRepository.save(Book(result.title, isbn))
    }
}
