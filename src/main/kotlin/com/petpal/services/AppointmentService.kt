package com.petpal.services

import com.petpal.dto.AppointmentRequest
import com.petpal.dto.AppointmentResponse
import com.petpal.entities.Appointment
import com.petpal.entities.AppointmentStatus
import com.petpal.exceptions.AppointmentNotFoundException
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.repositories.AppointmentRepository
import com.petpal.repositories.PetRepository
import com.petpal.repositories.VeterinarianRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val veterinarianRepository: VeterinarianRepository
) {
    private val logger = LoggerFactory.getLogger(AppointmentService::class.java)

    fun createAppointment(request: AppointmentRequest): AppointmentResponse {
        val pet = petRepository.findById(request.petId)
            .orElseThrow { PetNotFoundException("Pet no encontrado con id: ${request.petId}") }

        val veterinarians = request.veterinarianIds.map { vetId ->
            veterinarianRepository.findById(vetId)
                .orElseThrow { VeterinarianNotFoundException("Veterinario no encontrado con id: $vetId") }
        }.toSet()

        logger.info("Creando cita para pet: ${pet.name}")
        return appointmentRepository.save(request.toEntity(pet, veterinarians)).toResponse()
    }

    fun getAllAppointments(): List<AppointmentResponse> {
        logger.info("Obteniendo todas las citas")
        return appointmentRepository.findAll().map { it.toResponse() }
    }

    fun getAppointmentById(id: Long): AppointmentResponse {
        val appointment = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Cita no encontrada con id: $id") }
        return appointment.toResponse()
    }

    fun getAppointmentsByPetId(petId: Long): List<AppointmentResponse> {
        logger.info("Obteniendo citas del pet: $petId")
        return appointmentRepository.findByPetId(petId).map { it.toResponse() }
    }

    fun updateStatus(id: Long, status: String): AppointmentResponse {
        val existing = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Cita no encontrada con id: $id") }

        val newStatus = try {
            AppointmentStatus.valueOf(status.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw DomainValidationException(
                "Estado inválido: '$status'. Valores permitidos: ${AppointmentStatus.entries.joinToString()}"
            )
        }

        val updated = Appointment(
            id = existing.id,
            date = existing.date,
            reason = existing.reason,
            status = newStatus,
            pet = existing.pet,
            veterinarians = existing.veterinarians
        )
        return appointmentRepository.save(updated).toResponse()
    }

    fun deleteAppointment(id: Long) {
        if (!appointmentRepository.existsById(id)) {
            throw AppointmentNotFoundException("Cita no encontrada con id: $id")
        }
        appointmentRepository.deleteById(id)
    }
}
