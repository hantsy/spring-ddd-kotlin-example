package com.example.library.catalog.domain

import org.springframework.data.repository.CrudRepository

interface BookRepository : CrudRepository<Book, BookId> {

    fun existsByIsbnValue(value: String): Boolean
}
