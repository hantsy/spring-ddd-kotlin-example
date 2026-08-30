package com.example.library.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Exposes a [Clock] bean for time-related domain logic.
 */
@Configuration
class ClockConfig {

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}
