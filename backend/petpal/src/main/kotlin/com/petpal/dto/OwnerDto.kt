package com.petpal.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OwnerRequest(
    @field:NotBlank(message = "El nombre es obligatorio")
    val name: String = "",

    @field:NotBlank(message = "El email es obligatorio")
    @field:Email(message = "El email debe tener un formato válido")
    val email: String = "",

    @field:NotBlank(message = "El teléfono es obligatorio")
    @field:Pattern(regexp = "^[0-9+ -]{7,15}$", message = "El teléfono debe tener un formato válido")
    val phone: String = ""
)

data class OwnerResponse(
    val id: Long = 0L,
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)
