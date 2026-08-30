package com.example.library.catalog.domain

import org.jmolecules.ddd.types.ValueObject

// or: @ValueObject data class BarCode(val code: String)
data class BarCode(val code: String) : ValueObject
