package com.example.library.lending.domain

import org.springframework.data.repository.CrudRepository

interface LoanRepository : CrudRepository<Loan, LoanId> {

    fun existsByCopyIdAndReturnedAtIsNull(copyId: CopyId): Boolean

    fun isAvailable(copyId: CopyId): Boolean = !existsByCopyIdAndReturnedAtIsNull(copyId)

    fun findByIdOrThrow(loanId: LoanId): Loan = findById(loanId).orElseThrow { LoanNotFoundException(loanId) }
}
