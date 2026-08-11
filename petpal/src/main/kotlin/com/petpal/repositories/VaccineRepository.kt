package com.petpal.repositories

import com.petpal.entities.Vaccine
import org.springframework.data.jpa.repository.JpaRepository

interface VaccineRepository : JpaRepository<Vaccine, Long> {
    fun findByPetId(petId: Long): List<Vaccine>
}
