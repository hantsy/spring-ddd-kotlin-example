package com.example.library.catalog.application

import com.example.library.catalog.domain.CopyId
import com.example.library.catalog.domain.CopyNotFoundException
import com.example.library.catalog.domain.CopyRepository
import com.example.library.lending.domain.LoanClosed
import com.example.library.lending.domain.LoanCreated
import org.slf4j.LoggerFactory
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component

/**
 * Reacts to lending domain events to keep the catalog copy availability in sync.
 * This is the single sanctioned cross-context coupling, expressed via Spring
 * Modulith's [ApplicationModuleListener].
 */
@Component
class DomainEventListener(
    private val copyRepository: CopyRepository,
) {

    private val log = LoggerFactory.getLogger(DomainEventListener::class.java)

    @ApplicationModuleListener
    fun onLoanCreated(event: LoanCreated) {
        log.info("handling LoanCreated:{}", event)
        val copyId = CopyId(event.copyId.value)
        val copy = copyRepository.findById(copyId).orElseThrow { CopyNotFoundException(copyId) }
        copy.makeUnavailable()
        copyRepository.save(copy)
    }

    @ApplicationModuleListener
    fun onLoanClosed(event: LoanClosed) {
        log.info("handling LoanClosed:{}", event)
        val copyId = CopyId(event.copyId.value)
        val copy = copyRepository.findById(copyId).orElseThrow { CopyNotFoundException(copyId) }
        copy.makeAvailable()
        copyRepository.save(copy)
    }
}
