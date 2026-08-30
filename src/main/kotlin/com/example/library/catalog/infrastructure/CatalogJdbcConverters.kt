package com.example.library.catalog.infrastructure

import com.example.library.catalog.domain.BarCode
import com.example.library.catalog.domain.Isbn
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter

/**
 * Registers conversions for the catalog value objects so Spring Data JDBC
 * treats them as simple (single-column) values rather than nested entities.
 */
@Configuration
class CatalogJdbcConverters {

    @Bean
    fun isbnToStringConverter(): Converter<Isbn, String> = object : Converter<Isbn, String> {
        override fun convert(source: Isbn): String = source.value
    }

    @Bean
    fun stringToIsbnConverter(): Converter<String, Isbn> = object : Converter<String, Isbn> {
        override fun convert(source: String): Isbn = Isbn(source)
    }

    @Bean
    fun barCodeToStringConverter(): Converter<BarCode, String> = object : Converter<BarCode, String> {
        override fun convert(source: BarCode): String = source.code
    }

    @Bean
    fun stringToBarCodeConverter(): Converter<String, BarCode> = object : Converter<String, BarCode> {
        override fun convert(source: String): BarCode = BarCode(source)
    }
}
