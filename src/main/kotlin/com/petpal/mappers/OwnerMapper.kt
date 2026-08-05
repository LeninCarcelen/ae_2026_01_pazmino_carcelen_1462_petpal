package com.petpal.mappers

import com.petpal.dto.OwnerRequest
import com.petpal.dto.OwnerResponse
import com.petpal.entities.Owner

fun OwnerRequest.toEntity(): Owner = Owner(
    name = this.name,
    email = this.email,
    phone = this.phone
)

fun Owner.toResponse(): OwnerResponse = OwnerResponse(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone
)

fun Owner.updateFrom(request: OwnerRequest): Owner = Owner(
    id = this.id,
    name = request.name,
    email = request.email,
    phone = request.phone
)