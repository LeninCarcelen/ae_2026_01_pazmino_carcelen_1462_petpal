package com.petpal.repositories

import com.petpal.entities.Appointment
import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<Appointment, Long> {
    fun findByPetId(petId: Long): List<Appointment>
}
