package com.example.library.lending.domain

import org.jmolecules.ddd.types.ValueObject
import java.util.UUID

data class UserId(val value: UUID) : ValueObject {
    companion object {
        fun newInstance(): UserId = UserId(UUID.randomUUID())
    }
}
