package com.users.entities

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "app_user") // "user" es palabra reservada en Postgres
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,

    @Column(name = "cognito_sub", nullable = false, unique = true)
    open val cognitoSub: String = "",

    @Column(nullable = false, unique = true)
    open val email: String = "",

    @Column(name = "full_name", nullable = false)
    open val fullName: String = "",

    open val phone: String = "",

    @Column(nullable = false)
    open val role: String = "",

    @Column(name = "created_at", nullable = false)
    open val createdAt: Instant = Instant.now()
)
