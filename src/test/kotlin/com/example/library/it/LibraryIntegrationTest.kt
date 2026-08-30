package com.example.library.it

import com.example.library.catalog.domain.BarCode
import com.example.library.catalog.domain.Book
import com.example.library.catalog.domain.BookRepository
import com.example.library.catalog.domain.Copy
import com.example.library.catalog.domain.CopyId
import com.example.library.catalog.domain.CopyRepository
import com.example.library.catalog.domain.Isbn
import com.example.library.lending.application.RentBookUseCase
import com.example.library.lending.application.ReturnBookUseCase
import com.example.library.lending.domain.Loan
import com.example.library.lending.domain.LoanId
import com.example.library.lending.domain.LoanRepository
import com.example.library.lending.domain.OverdueFee
import com.example.library.lending.domain.UserId
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest(properties = ["spring.datasource.url=jdbc:h2:mem:library-it;DB_CLOSE_DELAY=-1"])
class LibraryIntegrationTest {

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var copyRepository: CopyRepository

    @Autowired
    private lateinit var loanRepository: LoanRepository

    @Autowired
    private lateinit var rentBookUseCase: RentBookUseCase

    @Autowired
    private lateinit var returnBookUseCase: ReturnBookUseCase

    @BeforeEach
    fun cleanUp() {
        loanRepository.deleteAll()
        copyRepository.deleteAll()
        bookRepository.deleteAll()
    }

    @Test
    fun testLibraryCrud() {
        val copyId = CopyId.newInstance()

        // Add a new Book
        val book = Book("Effective Java", Isbn("9780134685991"))
        bookRepository.save(book)

        // Add some copies of the book
        copyRepository.save(Copy(copyId, book.id, BarCode("BC001"), true))
        copyRepository.save(Copy(book.id, BarCode("BC002")))

        // verify all copies
        assertThat(copyRepository.findAll().toList()).hasSize(2)

        val userId = UserId.newInstance()
        // Rent a book
        rentBookUseCase.execute(com.example.library.lending.domain.CopyId.of(copyId.value), userId)

        // Verify that the book is NOT available (event handled asynchronously)
        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val copy = copyRepository.findById(copyId)
            assertThat(copy).isPresent
            assertThat(copy.get().available).isFalse()
        }

        // rent again should throw exception
        assertThrows<Exception> {
            rentBookUseCase.execute(com.example.library.lending.domain.CopyId.of(copyId.value), userId)
        }

        // verify ONLY one loan record
        val allLoans = loanRepository.findAll().toList()
        assertThat(allLoans).hasSize(1)

        // Retrieve Loan
        val loan = loanRepository.findByIdOrThrow(allLoans.first().id)
        assertThat(loan.copyId.value).isEqualTo(copyId.value)

        // Return the book
        returnBookUseCase.execute(loan.id)

        // Verify that the book is now available
        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            val copy = copyRepository.findById(copyId)
            assertThat(copy).isPresent
            assertThat(copy.get().available).isTrue()
        }
    }

    @Test
    fun testOverdueReturn() {
        val copyId = CopyId.newInstance()
        val book = Book("Domain-Driven Design", Isbn("9780321125217"))
        bookRepository.save(book)
        copyRepository.save(Copy(copyId, book.id, BarCode("BC003"), true))

        val overdueLoanIdHolder = AtomicReference<LoanId>()
        val userId = UserId.newInstance()

        // Create a loan with an expected return date 35 days in the past
        val pastDate = LocalDate.now().minusDays(35)
        val loan = Loan(
            com.example.library.lending.domain.CopyId.of(copyId.value),
            userId,
            LocalDateTime.now().minusDays(35),
            pastDate,
        )
        loanRepository.save(loan)
        overdueLoanIdHolder.set(loan.id)

        // Return the book — should trigger overdue fee
        returnBookUseCase.execute(loan.id)

        val returned = loanRepository.findByIdOrThrow(overdueLoanIdHolder.get())
        assertThat(returned.returnedAt).isNotNull()
        assertThat(returned.overdueFee).isEqualTo(OverdueFee.BEYOND_A_MONTH.amount)
    }
}
