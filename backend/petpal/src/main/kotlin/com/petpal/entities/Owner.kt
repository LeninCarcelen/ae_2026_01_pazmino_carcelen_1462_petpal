package com.petpal.entities

import jakarta.persistence.*

@Entity
@Table(name = "owner")
open class Owner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,
    open val name: String = "",
    open val email: String = "",
    open val phone: String = ""
)
