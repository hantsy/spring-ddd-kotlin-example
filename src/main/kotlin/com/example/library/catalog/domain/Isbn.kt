package com.example.library.catalog.domain

import org.apache.commons.validator.routines.ISBNValidator
import org.jmolecules.ddd.types.ValueObject

// or: @ValueObject data class Isbn(val value: String)
data class Isbn(val value: String) : ValueObject {
    init {
        require(ISBNValidator.getInstance().isValid(value)) { "invalid isbn: $value" }
    }
}
