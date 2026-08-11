package com.petpal.entities

import jakarta.persistence.*

@Entity
@Table(name = "veterinarian")
open class Veterinarian(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,
    open val name: String = "",
    open val specialty: String = "",

    @ManyToMany(mappedBy = "veterinarians")
    open val appointments: MutableSet<Appointment> = mutableSetOf()
)
