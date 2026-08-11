package com.petpal.services

import com.petpal.dto.VaccineRequest
import com.petpal.dto.VaccineResponse
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VaccineNotFoundException
import com.petpal.logging.LogEvent
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
            .orElseThrow { PetNotFoundException("Pet not found with id: ${request.petId}") }

        if (request.nextDueDate != null && request.dateApplied != null &&
            request.nextDueDate.isBefore(request.dateApplied)
        ) {
            throw DomainValidationException("nextDueDate cannot be before dateApplied")
        }

        val saved = vaccineRepository.save(request.toEntity(pet))
        logger.info(LogEvent.format("vaccine.created", "Vaccine registered", "name" to saved.name, "petId" to pet.id))
        return saved.toResponse()
    }

    fun getAllVaccines(): List<VaccineResponse> {
        logger.info(LogEvent.format("vaccine.list", "Listing all vaccines"))
        return vaccineRepository.findAll().map { it.toResponse() }
    }

    fun getVaccineById(id: Long): VaccineResponse {
        val vaccine = vaccineRepository.findById(id)
            .orElseThrow { VaccineNotFoundException("Vaccine not found with id: $id") }
        return vaccine.toResponse()
    }

    fun getVaccinesByPetId(petId: Long): List<VaccineResponse> {
        logger.info(LogEvent.format("vaccine.list_by_pet", "Listing vaccines by pet", "petId" to petId))
        return vaccineRepository.findByPetId(petId).map { it.toResponse() }
    }

    fun deleteVaccine(id: Long) {
        if (!vaccineRepository.existsById(id)) {
            throw VaccineNotFoundException("Vaccine not found with id: $id")
        }
        vaccineRepository.deleteById(id)
        logger.info(LogEvent.format("vaccine.deleted", "Vaccine deleted", "id" to id))
    }
}
