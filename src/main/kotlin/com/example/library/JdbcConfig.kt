package com.example.library

import com.example.library.catalog.domain.BarCode
import com.example.library.catalog.domain.Isbn
import com.example.library.lending.domain.CopyId
import com.example.library.lending.domain.UserId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import java.util.UUID

/**
 * Registers conversions for the domain value objects so that Spring Data JDBC
 * treats them as simple (single-column) values rather than nested entities.
 */
@Configuration
class JdbcConfig {

    @Bean
    fun jdbcCustomConversions(): JdbcCustomConversions {
        val converters = listOf(
            object : Converter<Isbn, String> {
                override fun convert(source: Isbn): String = source.value
            },
            object : Converter<String, Isbn> {
                override fun convert(source: String): Isbn = Isbn(source)
            },
            object : Converter<BarCode, String> {
                override fun convert(source: BarCode): String = source.code
            },
            object : Converter<String, BarCode> {
                override fun convert(source: String): BarCode = BarCode(source)
            },
            object : Converter<CopyId, UUID> {
                override fun convert(source: CopyId): UUID = source.value
            },
            object : Converter<UUID, CopyId> {
                override fun convert(source: UUID): CopyId = CopyId.of(source)
            },
            object : Converter<UserId, UUID> {
                override fun convert(source: UserId): UUID = source.value
            },
            object : Converter<UUID, UserId> {
                override fun convert(source: UUID): UserId = UserId(source)
            },
        )
        return JdbcCustomConversions(converters)
    }
}
