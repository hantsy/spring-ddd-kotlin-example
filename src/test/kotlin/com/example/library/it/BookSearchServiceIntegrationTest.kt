package com.example.library.it

import com.example.library.catalog.domain.BookNotFoundException
import com.example.library.catalog.domain.BookSearchService
import com.example.library.catalog.domain.Isbn
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Tag("integration")
class BookSearchServiceIntegrationTest {

    @Autowired
    private lateinit var bookSearchService: BookSearchService

    @Test
    fun `search effective java isbn returns book information`() {
        val result = bookSearchService.search(Isbn("9780134685991"))
        assertThat(result.title).isEqualTo("Effective Java")
    }

    @Test
    fun `search unknown isbn throws BookNotFoundException`() {
        assertThatThrownBy { bookSearchService.search(Isbn("9780999999998")) }
            .isInstanceOf(BookNotFoundException::class.java)
            .hasMessageContaining("9780999999998")
    }
}
