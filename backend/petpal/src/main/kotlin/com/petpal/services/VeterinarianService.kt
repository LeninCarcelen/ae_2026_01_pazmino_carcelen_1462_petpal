package com.petpal.services

import com.petpal.dto.VeterinarianRequest
import com.petpal.dto.VeterinarianResponse
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.logging.LogEvent
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.repositories.VeterinarianRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class VeterinarianService(
    private val veterinarianRepository: VeterinarianRepository
) {
    private val logger = LoggerFactory.getLogger(VeterinarianService::class.java)

    fun createVeterinarian(request: VeterinarianRequest): VeterinarianResponse {
        val saved = veterinarianRepository.save(request.toEntity())
        logger.info(LogEvent.format("veterinarian.created", "Veterinarian created", "name" to saved.name))
        return saved.toResponse()
    }

    fun getAllVeterinarians(): List<VeterinarianResponse> {
        logger.info(LogEvent.format("veterinarian.list", "Listing all veterinarians"))
        return veterinarianRepository.findAll().map { it.toResponse() }
    }

    fun getVeterinarianById(id: Long): VeterinarianResponse {
        val vet = veterinarianRepository.findById(id)
            .orElseThrow { VeterinarianNotFoundException("Veterinarian not found with id: $id") }
        return vet.toResponse()
    }

    fun deleteVeterinarian(id: Long) {
        if (!veterinarianRepository.existsById(id)) {
            throw VeterinarianNotFoundException("Veterinarian not found with id: $id")
        }
        veterinarianRepository.deleteById(id)
        logger.info(LogEvent.format("veterinarian.deleted", "Veterinarian deleted", "id" to id))
    }
}
