package com.example.library.lending

import com.example.library.lending.application.RentBookUseCase
import com.example.library.lending.application.ReturnBookUseCase
import com.example.library.lending.domain.CopyId
import com.example.library.lending.domain.Loan
import com.example.library.lending.domain.LoanClosed
import com.example.library.lending.domain.LoanCreated
import com.example.library.lending.domain.LoanRepository
import com.example.library.lending.domain.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario
import java.util.UUID

@ApplicationModuleTest
class LendingModuleTest {

    @Autowired
    private lateinit var rentBookUseCase: RentBookUseCase

    @Autowired
    private lateinit var returnBookUseCase: ReturnBookUseCase

    @Autowired
    private lateinit var loanRepository: LoanRepository

    @Test
    fun rentBookPublishesLoanCreated(scenario: Scenario) {
        val copyId = CopyId.of(UUID.randomUUID())
        val userId = UserId.newInstance()

        scenario.stimulate(Runnable { rentBookUseCase.execute(copyId, userId) })
            .andWaitForEventOfType(LoanCreated::class.java)
            .toArriveAndVerify { event: LoanCreated -> assertThat(event.copyId).isEqualTo(copyId) }
    }

    @Test
    fun returnBookPublishesLoanClosed(scenario: Scenario) {
        val copyId = CopyId.of(UUID.randomUUID())
        val userId = UserId.newInstance()
        val loan = loanRepository.save(Loan(copyId, userId))

        scenario.stimulate(Runnable { returnBookUseCase.execute(loan.id) })
            .andWaitForEventOfType(LoanClosed::class.java)
            .toArriveAndVerify { event: LoanClosed -> assertThat(event.copyId).isEqualTo(copyId) }
    }
}
