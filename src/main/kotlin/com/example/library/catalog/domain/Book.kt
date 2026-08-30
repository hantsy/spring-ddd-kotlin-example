package com.example.library.catalog.domain

import org.jmolecules.ddd.types.AggregateRoot

class Book(
    override val id: BookId,
    val title: String,
    val isbn: Isbn,
) : AggregateRoot<Book, BookId> {
    constructor(title: String, isbn: Isbn) : this(BookId.newInstance(), title, isbn)
}
