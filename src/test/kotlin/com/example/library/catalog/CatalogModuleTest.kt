package com.example.library.catalog

import com.example.library.catalog.domain.BarCode
import com.example.library.catalog.domain.BookId
import com.example.library.catalog.domain.Copy
import com.example.library.catalog.domain.CopyId
import com.example.library.catalog.domain.CopyRepository
import com.example.library.lending.domain.LoanClosed
import com.example.library.lending.domain.LoanCreated
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.modulith.test.Scenario

@ApplicationModuleTest
class CatalogModuleTest {

    @Autowired
    private lateinit var copyRepository: CopyRepository

    @Test
    fun loanCreatedMakesCopyUnavailable(scenario: Scenario) {
        val copyId = CopyId.newInstance()
        copyRepository.save(Copy(copyId, BookId.newInstance(), BarCode("BC001"), true))

        scenario.publish(LoanCreated(com.example.library.lending.domain.CopyId.of(copyId.value)))
            .andWaitForStateChange(
                { copyRepository.findById(copyId).map { it.available }.orElse(true) },
                { available: Boolean -> !available },
            )
            .andVerify { available: Boolean -> assertThat(available).isFalse() }
    }

    @Test
    fun loanClosedMakesCopyAvailable(scenario: Scenario) {
        val copyId = CopyId.newInstance()
        copyRepository.save(Copy(copyId, BookId.newInstance(), BarCode("BC001"), false))

        scenario.publish(LoanClosed(com.example.library.lending.domain.CopyId.of(copyId.value)))
            .andWaitForStateChange(
                { copyRepository.findById(copyId).map { it.available }.orElse(false) },
                { available: Boolean -> available },
            )
            .andVerify { available: Boolean -> assertThat(available).isTrue() }
    }
}
