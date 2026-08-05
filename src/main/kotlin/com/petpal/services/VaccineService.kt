package com.petpal.services

import com.petpal.dto.VaccineRequest
import com.petpal.dto.VaccineResponse
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VaccineNotFoundException
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.repositories.PetRepository
import com.petpal.repositories.VaccineRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class VaccineService(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(VaccineService::class.java)

    fun createVaccine(request: VaccineRequest): VaccineResponse {
        val pet = petRepository.findById(request.petId)
            .orElseThrow { PetNotFoundException("Pet no encontrado con id: ${request.petId}") }

        if (request.nextDueDate != null && request.dateApplied != null &&
            request.nextDueDate.isBefore(request.dateApplied)
        ) {
            throw DomainValidationException("La próxima fecha de refuerzo no puede ser anterior a la fecha de aplicación")
        }

        logger.info("Registrando vacuna: ${request.name} para pet: ${pet.name}")
        return vaccineRepository.save(request.toEntity(pet)).toResponse()
    }

    fun getAllVaccines(): List<VaccineResponse> {
        logger.info("Obteniendo todas las vacunas")
        return vaccineRepository.findAll().map { it.toResponse() }
    }

    fun getVaccineById(id: Long): VaccineResponse {
        val vaccine = vaccineRepository.findById(id)
            .orElseThrow { VaccineNotFoundException("Vacuna no encontrada con id: $id") }
        return vaccine.toResponse()
    }

    fun getVaccinesByPetId(petId: Long): List<VaccineResponse> {
        logger.info("Obteniendo vacunas del pet: $petId")
        return vaccineRepository.findByPetId(petId).map { it.toResponse() }
    }

    fun deleteVaccine(id: Long) {
        if (!vaccineRepository.existsById(id)) {
            throw VaccineNotFoundException("Vacuna no encontrada con id: $id")
        }
        vaccineRepository.deleteById(id)
    }
}
