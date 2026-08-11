package com.petpal.repositories

import com.petpal.entities.Pet
import org.springframework.data.jpa.repository.JpaRepository

interface PetRepository : JpaRepository<Pet, Long> {
    fun findByOwnerId(ownerId: Long): List<Pet>
}
