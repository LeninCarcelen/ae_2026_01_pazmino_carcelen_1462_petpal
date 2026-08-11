package com.users.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserRequest(
    @field:NotBlank(message = "cognitoSub is required")
    val cognitoSub: String = "",

    @field:NotBlank(message = "email is required")
    @field:Email(message = "email must be a valid address")
    val email: String = "",

    @field:NotBlank(message = "fullName is required")
    val fullName: String = "",

    val phone: String = "",

    @field:NotBlank(message = "role is required")
    val role: String = ""
)

data class UserResponse(
    val id: Long = 0L,
    val cognitoSub: String = "",
    val email: String = "",
    val fullName: String = "",
    val phone: String = "",
    val role: String = "",
    val createdAt: String = ""
)
