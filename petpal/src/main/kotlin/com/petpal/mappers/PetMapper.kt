package com.petpal.mappers

import com.petpal.dto.PetRequest
import com.petpal.dto.PetResponse
import com.petpal.entities.Owner
import com.petpal.entities.Pet

fun PetRequest.toEntity(owner: Owner): Pet = Pet(
    name = this.name,
    species = this.species,
    breed = this.breed,
    birthDate = this.birthDate,
    owner = owner
)

fun PetRequest.toEntity(existing: Pet, owner: Owner): Pet = Pet(
    id = existing.id,
    name = this.name,
    species = this.species,
    breed = this.breed,
    birthDate = this.birthDate,
    owner = owner
)

fun Pet.toResponse(): PetResponse = PetResponse(
    id = this.id,
    name = this.name,
    species = this.species,
    breed = this.breed,
    birthDate = this.birthDate,
    ownerId = this.owner?.id ?: 0L,
    ownerName = this.owner?.name ?: ""
)
