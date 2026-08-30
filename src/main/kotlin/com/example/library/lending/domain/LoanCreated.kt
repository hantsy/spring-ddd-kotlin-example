package com.example.library.lending.domain

import org.jmolecules.event.types.DomainEvent

data class LoanCreated(val copyId: CopyId) : DomainEvent
