package com.example.library.lending.domain

import org.jmolecules.ddd.types.Identifier
import java.util.UUID

// Identifier has no @Identifier annotation — it is interface-only.
data class LoanId(val value: UUID) : Identifier {
    companion object {
        fun newInstance(): LoanId = LoanId(UUID.randomUUID())
    }
}
