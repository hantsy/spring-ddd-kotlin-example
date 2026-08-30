package com.example.library.catalog.domain

interface BookSearchService {
    fun search(isbn: Isbn): BookInformation
}
