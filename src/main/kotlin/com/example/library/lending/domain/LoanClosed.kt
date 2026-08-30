package com.example.library.lending.domain

import org.jmolecules.event.types.DomainEvent

// or: @DomainEvent data class LoanClosed(val copyId: CopyId)
data class LoanClosed(val copyId: CopyId) : DomainEvent
