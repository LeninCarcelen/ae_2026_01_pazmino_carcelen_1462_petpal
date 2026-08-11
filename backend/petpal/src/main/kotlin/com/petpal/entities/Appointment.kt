package com.petpal.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "appointment")
open class Appointment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,
    open val date: LocalDateTime? = null,
    open val reason: String = "",

    @Enumerated(EnumType.STRING)
    open val status: AppointmentStatus = AppointmentStatus.SCHEDULED,

    @ManyToOne
    @JoinColumn(name = "pet_id")
    open val pet: Pet? = null,

    @ManyToMany
    @JoinTable(
        name = "appointment_veterinarian",
        joinColumns = [JoinColumn(name = "appointment_id")],
        inverseJoinColumns = [JoinColumn(name = "veterinarian_id")]
    )
    open val veterinarians: MutableSet<Veterinarian> = mutableSetOf()
)
