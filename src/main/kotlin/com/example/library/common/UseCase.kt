package com.example.library.common

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Marks a class as an application use case: a transactional Spring service
 * instrumented with the [Logged] logging aspect.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Service
@Transactional
@Logged
annotation class UseCase
