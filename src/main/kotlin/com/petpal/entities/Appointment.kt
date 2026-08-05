package com.petpal.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "appointment")
open class Appointment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0L,

    @Column(nullable = false)
    open val date: LocalDateTime? = null,

    @Column(nullable = false)
    open val reason: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open val status: AppointmentStatus = AppointmentStatus.SCHEDULED,

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    open val pet: Pet? = null,

    @ManyToMany
    @JoinTable(
        name = "appointment_veterinarian",
        joinColumns = [JoinColumn(name = "appointment_id")],
        inverseJoinColumns = [JoinColumn(name = "veterinarian_id")]
    )
    open val veterinarians: MutableSet<Veterinarian> = mutableSetOf()
)