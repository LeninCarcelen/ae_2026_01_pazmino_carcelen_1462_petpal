package com.petpal.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "vaccine")
open class Vaccine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,

    @Column(nullable = false)
    open val name: String = "",

    @Column(nullable = false)
    open val dateApplied: LocalDate? = null,

    open val nextDueDate: LocalDate? = null,

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    open val pet: Pet? = null
)