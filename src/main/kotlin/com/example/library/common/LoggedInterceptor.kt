package com.example.library.common

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Spring AOP equivalent of a CDI interceptor: logs entry, exit and exceptions
 * for every method of a [UseCase]-annotated bean.
 */
@Aspect
@Component
class LoggedInterceptor {

    private val log = LoggerFactory.getLogger(LoggedInterceptor::class.java)

    @Around("@within(com.example.library.common.UseCase)")
    fun logMethodCall(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        val params = joinPoint.args
        log.info("Entering method: {} with parameters: {}", methodName, params)
        return try {
            val result = joinPoint.proceed()
            log.info("Exiting method: {} with result: {}", methodName, result)
            result
        } catch (e: Throwable) {
            log.error("Exception in method: {}", methodName, e)
            throw e
        }
    }
}
