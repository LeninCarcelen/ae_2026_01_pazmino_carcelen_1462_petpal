package com.users.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        MDC.put("sub", resolveSub())
        val startedAt = System.currentTimeMillis()
        try {
            logger.info(LogEvent.format("http.request", "${request.method} ${request.requestURI}"))
            filterChain.doFilter(request, response)
        } finally {
            val durationMs = System.currentTimeMillis() - startedAt
            logger.info(
                LogEvent.format(
                    "http.response",
                    "${request.method} ${request.requestURI} -> ${response.status}",
                    "duration_ms" to durationMs
                )
            )
            MDC.remove("sub")
        }
    }

    private fun resolveSub(): String {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        return if (principal is Jwt) principal.subject else "anonimo"
    }
}
