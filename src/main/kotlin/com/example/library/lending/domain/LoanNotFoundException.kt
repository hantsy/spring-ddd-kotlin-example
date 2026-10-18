package com.example.library.lending.domain

import com.example.library.shared.DomainException

/**
 * Thrown when a loan cannot be found, e.g. the loan id passed to
 * [LoanRepository.findByIdOrThrow] does not exist.
 */
class LoanNotFoundException(loanId: LoanId) : DomainException("loan with id $loanId was not found")
