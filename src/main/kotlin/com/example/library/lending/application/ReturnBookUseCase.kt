package com.example.library.lending.application

import com.example.library.lending.domain.LoanClosed
import com.example.library.lending.domain.LoanId
import com.example.library.lending.domain.LoanRepository
import com.example.library.shared.UseCase
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import java.time.Clock
import java.time.LocalDateTime

@UseCase
class ReturnBookUseCase(
    private val loanRepository: LoanRepository,
    private val events: ApplicationEventPublisher,
    private val clock: Clock,
) {

    private val log = LoggerFactory.getLogger(ReturnBookUseCase::class.java)

    fun execute(loanId: LoanId) {
        val loan = loanRepository.findByIdOrThrow(loanId)
        loan.returned(LocalDateTime.now(clock))
        loanRepository.save(loan)

        log.info("firing returned event for loan with id = {}", loanId)
        events.publishEvent(LoanClosed(loan.copyId))
    }
}
