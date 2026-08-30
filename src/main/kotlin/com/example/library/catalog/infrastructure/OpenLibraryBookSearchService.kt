package com.example.library.catalog.infrastructure

import com.example.library.catalog.domain.BookInformation
import com.example.library.catalog.domain.BookNotFoundException
import com.example.library.catalog.domain.BookSearchException
import com.example.library.catalog.domain.BookSearchService
import com.example.library.catalog.domain.Isbn
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import java.net.http.HttpClient

/**
 * Open Library adapter implementing the [BookSearchService] domain port, backed
 * by Spring's [RestClient] and Jackson 3.
 */
@Component
class OpenLibraryBookSearchService : BookSearchService {

    companion object {
        const val DEFAULT_BASE_URL = "https://openlibrary.org/"
    }

    private val log = LoggerFactory.getLogger(OpenLibraryBookSearchService::class.java)
    private val restClient: RestClient

    constructor() : this(DEFAULT_BASE_URL)

    constructor(baseUrl: String) {
        val objectMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()
        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(JdkClientHttpRequestFactory(
                HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()))
            .messageConverters { converters -> converters.add(JacksonJsonHttpMessageConverter(objectMapper)) }
            .build()
    }

    override fun search(isbn: Isbn): BookInformation {
        return try {
            val result = restClient.get()
                .uri("/isbn/{isbn}.json", isbn.value)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(OpenLibraryIsbnSearchResult::class.java)
            log.debug("Book search result: {}", result)
            BookInformation(result!!.title)
        } catch (e: RestClientResponseException) {
            if (e.statusCode.value() == 404) {
                throw BookNotFoundException(isbn)
            }
            log.warn("OpenLibrary returned unexpected status {} for isbn {}", e.statusCode.value(), isbn.value)
            throw BookSearchException("failed to search book, upstream returned status ${e.statusCode.value()}")
        } catch (e: RestClientException) {
            log.error("network error searching isbn {}: {}", isbn.value, e.message)
            throw BookSearchException("failed to search book for isbn: ${isbn.value}", e)
        }
    }
}
