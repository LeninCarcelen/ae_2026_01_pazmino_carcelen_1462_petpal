package com.petpal.mappers

import com.petpal.dto.AppointmentRequest
import com.petpal.dto.AppointmentResponse
import com.petpal.dto.VeterinarianRequest
import com.petpal.dto.VeterinarianResponse
import com.petpal.entities.Appointment
import com.petpal.entities.AppointmentStatus
import com.petpal.entities.Pet
import com.petpal.entities.Veterinarian

fun AppointmentRequest.toEntity(pet: Pet, veterinarians: Set<Veterinarian>): Appointment = Appointment(
    date = this.date,
    reason = this.reason,
    status = AppointmentStatus.SCHEDULED,
    pet = pet,
    veterinarians = veterinarians.toMutableSet()
)

fun Appointment.toResponse(): AppointmentResponse = AppointmentResponse(
    id = this.id,
    date = this.date,
    reason = this.reason,
    status = this.status.name,
    petId = this.pet?.id ?: 0L,
    petName = this.pet?.name ?: "",
    veterinarians = this.veterinarians.map { it.toResponse() }
)

fun VeterinarianRequest.toEntity(): Veterinarian = Veterinarian(
    name = this.name,
    specialty = this.specialty
)

fun Veterinarian.toResponse(): VeterinarianResponse = VeterinarianResponse(
    id = this.id,
    name = this.name,
    specialty = this.specialty
)
