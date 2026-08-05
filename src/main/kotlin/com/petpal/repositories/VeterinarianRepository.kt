package com.petpal.repositories

import com.petpal.entities.Veterinarian
import org.springframework.data.jpa.repository.JpaRepository

interface VeterinarianRepository : JpaRepository<Veterinarian, Long>
