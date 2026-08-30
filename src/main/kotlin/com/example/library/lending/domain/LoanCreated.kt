package com.example.library.lending.domain

import org.jmolecules.event.types.DomainEvent

// or: @DomainEvent data class LoanCreated(val copyId: CopyId)
data class LoanCreated(val copyId: CopyId) : DomainEvent
