package com.example.library.catalog.domain

import org.jmolecules.ddd.types.Identifier
import java.util.UUID

// Identifier has no @Identifier annotation — it is interface-only.
data class CopyId(val value: UUID) : Identifier {
    companion object {
        fun newInstance(): CopyId = CopyId(UUID.randomUUID())
    }
}
