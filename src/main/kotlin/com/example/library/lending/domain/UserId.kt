package com.example.library.lending.domain

import org.jmolecules.ddd.annotation.ValueObject
import java.util.UUID

@ValueObject
data class UserId(val value: UUID) {
    companion object {
        fun newInstance(): UserId = UserId(UUID.randomUUID())
    }
}
