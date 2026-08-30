package com.example.library.lending.domain

import org.jmolecules.ddd.types.Identifier
import java.util.UUID

data class LoanId(val value: UUID) : Identifier {
    companion object {
        fun newInstance(): LoanId = LoanId(UUID.randomUUID())
    }
}
