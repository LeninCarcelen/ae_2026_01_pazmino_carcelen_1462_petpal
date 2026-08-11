package com.petpal.services

import com.petpal.audit.AuditService
import com.petpal.dto.PetRequest
import com.petpal.dto.PetResponse
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.logging.LogEvent
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.repositories.OwnerRepository
import com.petpal.repositories.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PetService(
    private val petRepository: PetRepository,
    private val ownerRepository: OwnerRepository,
    private val auditService: AuditService
) {
    private val logger = LoggerFactory.getLogger(PetService::class.java)

    fun createPet(request: PetRequest): PetResponse {
        val owner = ownerRepository.findById(request.ownerId)
            .orElseThrow { OwnerNotFoundException("Owner not found with id: ${request.ownerId}") }

        val saved = petRepository.save(request.toEntity(owner))
        logger.info(LogEvent.format("pet.created", "Pet created", "name" to saved.name, "ownerId" to owner.id))
        auditService.record("Pet", saved.id, "CREATE", after = saved.toResponse().toString())
        return saved.toResponse()
    }

    fun getAllPets(): List<PetResponse> {
        logger.info(LogEvent.format("pet.list", "Listing all pets"))
        return petRepository.findAll().map { it.toResponse() }
    }

    fun getPetById(id: Long): PetResponse {
        val pet = petRepository.findById(id)
            .orElseThrow { PetNotFoundException("Pet not found with id: $id") }
        return pet.toResponse()
    }

    fun getPetsByOwnerId(ownerId: Long): List<PetResponse> {
        logger.info(LogEvent.format("pet.list_by_owner", "Listing pets by owner", "ownerId" to ownerId))
        return petRepository.findByOwnerId(ownerId).map { it.toResponse() }
    }

    fun updatePet(id: Long, request: PetRequest): PetResponse {
        val existing = petRepository.findById(id)
            .orElseThrow { PetNotFoundException("Pet not found with id: $id") }
        val owner = ownerRepository.findById(request.ownerId)
            .orElseThrow { OwnerNotFoundException("Owner not found with id: ${request.ownerId}") }
        val before = existing.toResponse().toString()

        val saved = petRepository.save(request.toEntity(existing, owner))
        logger.info(LogEvent.format("pet.updated", "Pet updated", "id" to id))
        auditService.record("Pet", id, "UPDATE", before = before, after = saved.toResponse().toString())
        return saved.toResponse()
    }

    fun deletePet(id: Long) {
        val existing = petRepository.findById(id)
            .orElseThrow { PetNotFoundException("Pet not found with id: $id") }
        val before = existing.toResponse().toString()
        petRepository.deleteById(id)
        logger.info(LogEvent.format("pet.deleted", "Pet deleted", "id" to id))
        auditService.record("Pet", id, "DELETE", before = before)
    }
}
