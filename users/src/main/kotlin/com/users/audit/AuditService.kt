package com.users.audit

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class AuditService(
    private val auditLogRepository: AuditLogRepository
) {
    fun record(entityName: String, entityId: Long, operation: String, before: String? = null, after: String? = null) {
        auditLogRepository.save(
            AuditLog(
                entityName = entityName,
                entityId = entityId,
                operation = operation,
                performedBy = currentSub(),
                beforeData = before,
                afterData = after
            )
        )
    }

    private fun currentSub(): String {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        return if (principal is Jwt) principal.subject else "anonimo"
    }
}
