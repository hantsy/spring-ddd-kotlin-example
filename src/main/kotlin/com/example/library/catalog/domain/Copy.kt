package com.example.library.catalog.domain

import org.jmolecules.ddd.types.AggregateRoot
import org.jmolecules.ddd.types.Association

// or: @AggregateRoot class Copy(@field:Identity val id: CopyId, ...)
class Copy(
    override val id: CopyId,
    val bookId: Association<Book, BookId>,
    val barCode: BarCode,
    var available: Boolean,
) : AggregateRoot<Copy, CopyId> {
    constructor(bookId: BookId, barCode: BarCode) :
        this(CopyId.newInstance(), Association.forId<Book, BookId>(bookId), barCode, true)

    constructor(copyId: CopyId, bookId: BookId, barCode: BarCode, available: Boolean) :
        this(copyId, Association.forId<Book, BookId>(bookId), barCode, available)

    fun makeUnavailable() {
        available = false
    }

    fun makeAvailable() {
        available = true
    }
}
