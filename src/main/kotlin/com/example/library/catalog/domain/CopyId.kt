package com.example.library.catalog.domain

import org.jmolecules.ddd.types.Identifier
import java.util.UUID

data class CopyId(val value: UUID) : Identifier {
    companion object {
        fun newInstance(): CopyId = CopyId(UUID.randomUUID())
    }
}
