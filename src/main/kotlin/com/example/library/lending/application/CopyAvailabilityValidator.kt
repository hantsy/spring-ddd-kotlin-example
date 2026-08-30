package com.example.library.lending.application

import com.example.library.lending.domain.CopyId
import com.example.library.lending.domain.CopyNotAvailableException
import com.example.library.lending.domain.LoanRepository
import org.springframework.stereotype.Component

@Component
class CopyAvailabilityValidator(
    private val loanRepository: LoanRepository,
) {
    fun checkAvailable(copyId: CopyId) {
        if (!loanRepository.isAvailable(copyId)) {
            throw CopyNotAvailableException(copyId)
        }
    }
}
