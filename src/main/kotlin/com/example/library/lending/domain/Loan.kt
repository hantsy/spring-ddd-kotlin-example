package com.example.library.lending.domain

import org.jmolecules.ddd.types.AggregateRoot
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// or: @AggregateRoot class Loan(@field:Identity val id: LoanId, ...)
class Loan(
    override val id: LoanId,
    val copyId: CopyId,
    val userId: UserId,
    val createdAt: LocalDateTime,
    val expectedReturnDate: LocalDate,
    var returnedAt: LocalDateTime?,
    var overdueFee: BigDecimal?,
) : AggregateRoot<Loan, LoanId> {

    constructor(copyId: CopyId, userId: UserId, createdAt: LocalDateTime) :
        this(LoanId.newInstance(), copyId, userId, createdAt, createdAt.toLocalDate().plusDays(LOAN_TERM_DAYS), null, null)

    constructor(copyId: CopyId, userId: UserId, createdAt: LocalDateTime, expectedReturnDate: LocalDate) :
        this(LoanId.newInstance(), copyId, userId, createdAt, expectedReturnDate, null, null)

    fun returned(returnedAt: LocalDateTime) {
        this.returnedAt = returnedAt
        if (returnedAt.isAfter(expectedReturnDate.atStartOfDay())) {
            val daysOverdue = ChronoUnit.DAYS.between(expectedReturnDate, returnedAt.toLocalDate())
            overdueFee = OverdueFee.forDays(daysOverdue)?.amount
            // In production, fire an OverdueFeeCalculated domain event here
        }
    }

    companion object {
        private const val LOAN_TERM_DAYS: Long = 30L
    }
}
