package com.example.library.lending.domain

import com.example.library.common.DomainException

/**
 * Thrown when a copy cannot be rented because it is already on loan.
 */
class CopyNotAvailableException(copyId: CopyId) : DomainException("copy with id = $copyId is not available")
