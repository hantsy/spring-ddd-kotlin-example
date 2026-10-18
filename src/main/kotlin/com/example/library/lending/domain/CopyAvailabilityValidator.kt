package com.example.library.lending.domain

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
