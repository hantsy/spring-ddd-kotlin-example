package com.example.library.lending.domain

import org.jmolecules.event.types.DomainEvent

data class LoanClosed(val copyId: CopyId) : DomainEvent
