package com.petpal.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "pet")
open class Pet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,

    @Column(nullable = false)
    open val name: String = "",

    @Column(nullable = false)
    open val species: String = "",

    open val breed: String = "",

    open val birthDate: LocalDate? = null,

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    open val owner: Owner? = null
)