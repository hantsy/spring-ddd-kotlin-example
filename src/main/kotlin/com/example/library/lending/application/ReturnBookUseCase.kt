package com.example.library.lending.application

import com.example.library.common.UseCase
import com.example.library.lending.domain.LoanClosed
import com.example.library.lending.domain.LoanId
import com.example.library.lending.domain.LoanRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher

@UseCase
class ReturnBookUseCase(
    private val loanRepository: LoanRepository,
    private val events: ApplicationEventPublisher,
) {

    private val log = LoggerFactory.getLogger(ReturnBookUseCase::class.java)

    fun execute(loanId: LoanId) {
        val loan = loanRepository.findByIdOrThrow(loanId)
        loan.returned()
        loanRepository.save(loan)

        log.info("firing returned event for loan with id = {}", loanId)
        events.publishEvent(LoanClosed(loan.copyId))
    }
}
