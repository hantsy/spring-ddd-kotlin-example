package com.example.library.catalog.domain

import org.jmolecules.ddd.types.Identifier
import java.util.UUID

data class BookId(val value: UUID) : Identifier {
    companion object {
        fun newInstance(): BookId = BookId(UUID.randomUUID())
    }
}
