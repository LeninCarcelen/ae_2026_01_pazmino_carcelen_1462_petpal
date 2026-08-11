package com.petpal.dto

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class AppointmentRequest(
    @field:NotNull(message = "La fecha es obligatoria")
    @field:FutureOrPresent(message = "La fecha de la cita no puede ser en el pasado")
    val date: LocalDateTime? = null,

    @field:NotBlank(message = "El motivo es obligatorio")
    val reason: String = "",

    @field:Positive(message = "Debe indicar un petId válido")
    val petId: Long = 0L,

    @field:NotEmpty(message = "Debe asignar al menos un veterinario")
    val veterinarianIds: List<Long> = emptyList()
)

data class AppointmentResponse(
    val id: Long = 0L,
    val date: LocalDateTime? = null,
    val reason: String = "",
    val status: String = "",
    val petId: Long = 0L,
    val petName: String = "",
    val veterinarians: List<VeterinarianResponse> = emptyList()
)

data class VeterinarianRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val name: String = "",

    @field:NotBlank(message = "La especialidad es obligatoria")
    val specialty: String = ""
)

data class VeterinarianResponse(
    val id: Long = 0L,
    val name: String = "",
    val specialty: String = ""
)
