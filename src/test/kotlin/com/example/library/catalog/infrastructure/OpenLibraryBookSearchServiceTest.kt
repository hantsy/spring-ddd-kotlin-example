package com.example.library.catalog.infrastructure

import com.example.library.catalog.domain.BookInformation
import com.example.library.catalog.domain.BookNotFoundException
import com.example.library.catalog.domain.BookSearchException
import com.example.library.catalog.domain.BookSearchService
import com.example.library.catalog.domain.Isbn
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.web.client.ResourceAccessException

@WireMockTest(httpPort = 8080)
class OpenLibraryBookSearchServiceTest {

    private val service: BookSearchService = OpenLibraryBookSearchService(BASE_URL)

    @Test
    fun `search with known isbn should return book information`() {
        stubFor(get(urlEqualTo("/isbn/$KNOWN_ISBN.json"))
            .willReturn(aResponse()
                .withStatus(302)
                .withHeader("Location", "/books/OL31838212M.json")))

        stubFor(get(urlEqualTo("/books/OL31838212M.json"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""{"title":"Effective Java","publishers":["Addison-Wesley"],"isbn_13":["9780134685991"],"revisions":1}""")))

        val result = service.search(Isbn(KNOWN_ISBN))

        assertThat(result.title).isEqualTo("Effective Java")

        verify(getRequestedFor(urlEqualTo("/isbn/$KNOWN_ISBN.json"))
            .withHeader("Accept", equalTo("application/json")))
        verify(getRequestedFor(urlEqualTo("/books/OL31838212M.json"))
            .withHeader("Accept", equalTo("application/json")))
    }

    @Test
    fun `search with unknown isbn should throw BookNotFoundException when upstream returns 404`() {
        stubFor(get(urlEqualTo("/isbn/$UNKNOWN_ISBN.json"))
            .willReturn(aResponse().withStatus(404)))

        assertThatThrownBy { service.search(Isbn(UNKNOWN_ISBN)) }
            .isInstanceOf(BookNotFoundException::class.java)
            .hasMessageContaining(UNKNOWN_ISBN)

        verify(getRequestedFor(urlEqualTo("/isbn/$UNKNOWN_ISBN.json")))
    }

    @Test
    fun `search should throw BookSearchException when upstream returns error status`() {
        stubFor(get(urlEqualTo("/isbn/$UNKNOWN_ISBN.json"))
            .willReturn(aResponse().withStatus(500)))

        assertThatThrownBy { service.search(Isbn(UNKNOWN_ISBN)) }
            .isInstanceOf(BookSearchException::class.java)
            .hasMessageContaining("500")
    }

    @Test
    fun `search should throw BookSearchException when network fails`() {
        stubFor(get(urlEqualTo("/isbn/$UNKNOWN_ISBN.json"))
            .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)))

        assertThatThrownBy { service.search(Isbn(UNKNOWN_ISBN)) }
            .isInstanceOf(BookSearchException::class.java)
            .hasMessageContaining(UNKNOWN_ISBN)
            .hasCauseInstanceOf(ResourceAccessException::class.java)
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080/"
        private const val UNKNOWN_ISBN = "9780000000002"
        private const val KNOWN_ISBN = "9780134685991"
    }
}
