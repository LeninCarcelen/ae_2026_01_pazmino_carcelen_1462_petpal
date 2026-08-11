package com.petpal.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class PetRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val name: String = "",

    @field:NotBlank(message = "La especie es obligatoria")
    val species: String = "",

    val breed: String = "",

    @field:PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    val birthDate: LocalDate? = null,

    @field:Positive(message = "Debe indicar un ownerId válido")
    val ownerId: Long = 0L
)

data class PetResponse(
    val id: Long = 0L,
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val birthDate: LocalDate? = null,
    val ownerId: Long = 0L,
    val ownerName: String = ""
)
