package com.users.mappers

import com.users.dto.UserRequest
import com.users.dto.UserResponse
import com.users.entities.User

fun UserRequest.toEntity(): User = User(
    cognitoSub = this.cognitoSub,
    email = this.email,
    fullName = this.fullName,
    phone = this.phone,
    role = this.role
)

fun User.toResponse(): UserResponse = UserResponse(
    id = this.id,
    cognitoSub = this.cognitoSub,
    email = this.email,
    fullName = this.fullName,
    phone = this.phone,
    role = this.role,
    createdAt = this.createdAt.toString()
)

fun User.copyWith(
    fullName: String = this.fullName,
    phone: String = this.phone,
    role: String = this.role
): User = User(
    id = this.id,
    cognitoSub = this.cognitoSub,
    email = this.email,
    fullName = fullName,
    phone = phone,
    role = role,
    createdAt = this.createdAt
)
