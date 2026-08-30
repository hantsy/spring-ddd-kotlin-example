package com.example.library.catalog.application

import com.example.library.catalog.domain.BarCode
import com.example.library.catalog.domain.BookId
import com.example.library.catalog.domain.Copy
import com.example.library.catalog.domain.CopyRepository
import com.example.library.common.UseCase

@UseCase
class RegisterBookCopyUseCase(
    private val copyRepository: CopyRepository,
) {
    fun execute(bookId: BookId, barCode: BarCode) {
        copyRepository.save(Copy(bookId, barCode))
    }
}
