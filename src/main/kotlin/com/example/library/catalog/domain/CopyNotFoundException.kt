package com.example.library.catalog.domain

import com.example.library.common.DomainException

/**
 * Thrown when a copy cannot be found in the catalog, e.g. a [CopyRepository]
 * lookup by id came up empty while reacting to a lending event.
 */
class CopyNotFoundException(copyId: CopyId) : DomainException("copy with id $copyId was not found")
