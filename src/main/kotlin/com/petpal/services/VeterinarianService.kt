package com.petpal.services

import com.petpal.dto.VeterinarianRequest
import com.petpal.dto.VeterinarianResponse
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.mappers.updateFrom
import com.petpal.repositories.VeterinarianRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class VeterinarianService(
    private val veterinarianRepository: VeterinarianRepository
) {
    private val logger = LoggerFactory.getLogger(VeterinarianService::class.java)

    fun createVeterinarian(request: VeterinarianRequest): VeterinarianResponse {
        logger.info("Creando veterinario: ${request.name}")
        return veterinarianRepository.save(request.toEntity()).toResponse()
    }

    fun getAllVeterinarians(): List<VeterinarianResponse> {
        logger.info("Obteniendo todos los veterinarios")
        return veterinarianRepository.findAll().map { it.toResponse() }
    }

    fun getVeterinarianById(id: Long): VeterinarianResponse {
        val vet = veterinarianRepository.findById(id)
            .orElseThrow { VeterinarianNotFoundException("Veterinario no encontrado con id: $id") }
        return vet.toResponse()
    }

    fun updateVeterinarian(id: Long, request: VeterinarianRequest): VeterinarianResponse {
        logger.info("Actualizando veterinario con id: $id")
        val vet = veterinarianRepository.findById(id)
            .orElseThrow { VeterinarianNotFoundException("Veterinario no encontrado con id: $id") }

        val updatedVet = vet.updateFrom(request)
        return veterinarianRepository.save(updatedVet).toResponse()
    }

    fun deleteVeterinarian(id: Long) {
        if (!veterinarianRepository.existsById(id)) {
            throw VeterinarianNotFoundException("Veterinario no encontrado con id: $id")
        }
        veterinarianRepository.deleteById(id)
    }
}