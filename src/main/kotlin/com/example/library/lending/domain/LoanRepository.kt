package com.example.library.lending.domain

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface LoanRepository : CrudRepository<Loan, LoanId> {

    fun existsByCopyIdValueAndReturnedAtIsNull(value: UUID): Boolean

    fun isAvailable(copyId: CopyId): Boolean = !existsByCopyIdValueAndReturnedAtIsNull(copyId.value)

    fun findByIdOrThrow(loanId: LoanId): Loan = findById(loanId).orElseThrow { LoanNotFoundException(loanId) }
}
