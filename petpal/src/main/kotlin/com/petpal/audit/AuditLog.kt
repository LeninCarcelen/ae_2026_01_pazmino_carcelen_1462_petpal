package com.petpal.audit

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "audit_log")
open class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,

    @Column(name = "entity_name", nullable = false)
    open val entityName: String = "",

    @Column(name = "entity_id", nullable = false)
    open val entityId: Long = 0L,

    @Column(nullable = false)
    open val operation: String = "",

    @Column(name = "performed_by", nullable = false)
    open val performedBy: String = "",

    @Column(name = "performed_at", nullable = false)
    open val performedAt: Instant = Instant.now(),

    @Column(name = "before_data", columnDefinition = "text")
    open val beforeData: String? = null,

    @Column(name = "after_data", columnDefinition = "text")
    open val afterData: String? = null
)
