package com.example.library.lending.domain

import org.jmolecules.ddd.annotation.ValueObject
import java.util.UUID

/**
 * Holds a reference to a catalog [Copy] identity. The lending context is a
 * consumer of copy identities — it never generates its own, which is why there
 * is no factory for a random id.
 */
@ValueObject
data class CopyId(val value: UUID) {
    companion object {
        fun of(id: UUID): CopyId = CopyId(id)
    }
}
