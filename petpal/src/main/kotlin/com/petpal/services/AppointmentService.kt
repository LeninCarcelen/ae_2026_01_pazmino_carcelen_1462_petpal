package com.petpal.services

import com.petpal.dto.AppointmentRequest
import com.petpal.dto.AppointmentResponse
import com.petpal.entities.Appointment
import com.petpal.entities.AppointmentStatus
import com.petpal.exceptions.AppointmentNotFoundException
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.logging.LogEvent
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
            .orElseThrow { PetNotFoundException("Pet not found with id: ${request.petId}") }

        val veterinarians = request.veterinarianIds.map { vetId ->
            veterinarianRepository.findById(vetId)
                .orElseThrow { VeterinarianNotFoundException("Veterinarian not found with id: $vetId") }
        }.toSet()

        val saved = appointmentRepository.save(request.toEntity(pet, veterinarians))
        logger.info(LogEvent.format("appointment.created", "Appointment created", "petId" to pet.id))
        return saved.toResponse()
    }

    fun getAllAppointments(): List<AppointmentResponse> {
        logger.info(LogEvent.format("appointment.list", "Listing all appointments"))
        return appointmentRepository.findAll().map { it.toResponse() }
    }

    fun getAppointmentById(id: Long): AppointmentResponse {
        val appointment = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Appointment not found with id: $id") }
        return appointment.toResponse()
    }

    fun getAppointmentsByPetId(petId: Long): List<AppointmentResponse> {
        logger.info(LogEvent.format("appointment.list_by_pet", "Listing appointments by pet", "petId" to petId))
        return appointmentRepository.findByPetId(petId).map { it.toResponse() }
    }

    fun updateStatus(id: Long, status: String): AppointmentResponse {
        val existing = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Appointment not found with id: $id") }

        val newStatus = try {
            AppointmentStatus.valueOf(status.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw DomainValidationException(
                "Invalid status: '$status'. Allowed values: ${AppointmentStatus.entries.joinToString()}"
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
        val saved = appointmentRepository.save(updated)
        logger.info(LogEvent.format("appointment.status_updated", "Appointment status updated", "id" to id, "status" to newStatus))
        return saved.toResponse()
    }

    fun deleteAppointment(id: Long) {
        if (!appointmentRepository.existsById(id)) {
            throw AppointmentNotFoundException("Appointment not found with id: $id")
        }
        appointmentRepository.deleteById(id)
        logger.info(LogEvent.format("appointment.deleted", "Appointment deleted", "id" to id))
    }
}
