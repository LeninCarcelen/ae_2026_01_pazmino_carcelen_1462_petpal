package com.petpal.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class VaccineRequest(
    @field:NotBlank(message = "El nombre de la vacuna es obligatorio")
    val name: String = "",

    @field:NotNull(message = "La fecha de aplicación es obligatoria")
    @field:PastOrPresent(message = "La fecha de aplicación no puede ser futura")
    val dateApplied: LocalDate? = null,

    val nextDueDate: LocalDate? = null,

    @field:Positive(message = "Debe indicar un petId válido")
    val petId: Long = 0L
)

data class VaccineResponse(
    val id: Long = 0L,
    val name: String = "",
    val dateApplied: LocalDate? = null,
    val nextDueDate: LocalDate? = null,
    val petId: Long = 0L,
    val petName: String = ""
)
