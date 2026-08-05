package com.petpal.repositories

import com.petpal.entities.Owner
import org.springframework.data.jpa.repository.JpaRepository

interface OwnerRepository : JpaRepository<Owner, Long> {
    fun existsByEmail(email: String): Boolean
}
