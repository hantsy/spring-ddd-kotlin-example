package com.example.library.lending.application

import com.example.library.common.UseCase
import com.example.library.lending.domain.CopyId
import com.example.library.lending.domain.Loan
import com.example.library.lending.domain.LoanCreated
import com.example.library.lending.domain.LoanRepository
import com.example.library.lending.domain.UserId
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

@UseCase
class RentBookUseCase(
    private val loanRepository: LoanRepository,
    private val copyAvailabilityValidator: CopyAvailabilityValidator,
    private val events: ApplicationEventPublisher,
    private val clock: Clock,
) {

    private val log = LoggerFactory.getLogger(RentBookUseCase::class.java)

    fun execute(copyId: CopyId, userId: UserId) {
        copyAvailabilityValidator.checkAvailable(copyId)
        val now = LocalDateTime.now(clock)
        loanRepository.save(Loan(copyId, userId, now, LocalDate.now(clock).plusDays(30)))

        log.info("firing LoanCreated with copy id = {}", copyId)
        events.publishEvent(LoanCreated(copyId))
    }
}
