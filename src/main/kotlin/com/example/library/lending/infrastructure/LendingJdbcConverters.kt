package com.example.library.lending.infrastructure

import com.example.library.lending.domain.CopyId
import com.example.library.lending.domain.UserId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import java.util.UUID

/**
 * Registers conversions for the lending value objects so Spring Data JDBC
 * treats them as simple (single-column) values rather than nested entities.
 */
@Configuration
class LendingJdbcConverters {

    @Bean
    fun copyIdToUUIDConverter(): Converter<CopyId, UUID> = object : Converter<CopyId, UUID> {
        override fun convert(source: CopyId): UUID = source.value
    }

    @Bean
    fun uuidToCopyIdConverter(): Converter<UUID, CopyId> = object : Converter<UUID, CopyId> {
        override fun convert(source: UUID): CopyId = CopyId.of(source)
    }

    @Bean
    fun userIdToUUIDConverter(): Converter<UserId, UUID> = object : Converter<UserId, UUID> {
        override fun convert(source: UserId): UUID = source.value
    }

    @Bean
    fun uuidToUserIdConverter(): Converter<UUID, UserId> = object : Converter<UUID, UserId> {
        override fun convert(source: UUID): UserId = UserId(source)
    }
}
