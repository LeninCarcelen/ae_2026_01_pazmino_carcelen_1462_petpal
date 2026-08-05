package com.petpal.entities

import jakarta.persistence.*

@Entity
@Table(name = "owner")
open class Owner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,

    @Column(nullable = false)
    open val name: String = "",

    @Column(nullable = false, unique = true)
    open val email: String = "",

    @Column(nullable = false)
    open val phone: String = ""
)