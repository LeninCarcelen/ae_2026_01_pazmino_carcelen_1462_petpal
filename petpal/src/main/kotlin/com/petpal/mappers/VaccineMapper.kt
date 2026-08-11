package com.petpal.mappers

import com.petpal.dto.VaccineRequest
import com.petpal.dto.VaccineResponse
import com.petpal.entities.Pet
import com.petpal.entities.Vaccine

fun VaccineRequest.toEntity(pet: Pet): Vaccine = Vaccine(
    name = this.name,
    dateApplied = this.dateApplied,
    nextDueDate = this.nextDueDate,
    pet = pet
)

fun Vaccine.toResponse(): VaccineResponse = VaccineResponse(
    id = this.id,
    name = this.name,
    dateApplied = this.dateApplied,
    nextDueDate = this.nextDueDate,
    petId = this.pet?.id ?: 0L,
    petName = this.pet?.name ?: ""
)
