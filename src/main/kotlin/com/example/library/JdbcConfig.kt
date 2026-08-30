package com.example.library

import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

/**
 * Collects the per-module [Converter] beans registered by the catalog and
 * lending infrastructure packages into Spring Data JDBC's custom conversions.
 */
@Configuration
class JdbcConfig(private val converters: ObjectProvider<Converter<*, *>>) : AbstractJdbcConfiguration() {

    override fun userConverters(): List<Any> = converters.orderedStream().toList()
}
