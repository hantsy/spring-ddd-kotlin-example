package com.example.library.shared

/**
 * Marker annotation that triggers method-call logging via [LoggedInterceptor].
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Logged
