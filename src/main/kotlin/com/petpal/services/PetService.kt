package com.petpal.services

import com.petpal.dto.PetRequest
import com.petpal.dto.PetResponse
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.repositories.OwnerRepository
import com.petpal.repositories.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PetService(
    private val petRepository: PetRepository,
    private val ownerRepository: OwnerRepository
) {
    private val logger = LoggerFactory.getLogger(PetService::class.java)

    fun createPet(request: PetRequest): PetResponse {
        val owner = ownerRepository.findById(request.ownerId)
            .orElseThrow { OwnerNotFoundException("Owner no encontrado con id: ${request.ownerId}") }
        logger.info("Creando pet: ${request.name} para owner: ${owner.name}")
        return petRepository.save(request.toEntity(owner)).toResponse()
    }

    fun getAllPets(): List<PetResponse> {
        logger.info("Obteniendo todos los pets")
        return petRepository.findAll().map { it.toResponse() }
    }

    fun getPetById(id: Long): PetResponse {
        val pet = petRepository.findById(id)
            .orElseThrow { PetNotFoundException("Pet no encontrado con id: $id") }
        return pet.toResponse()
    }

    fun getPetsByOwnerId(ownerId: Long): List<PetResponse> {
        logger.info("Obteniendo pets del owner: $ownerId")
        return petRepository.findByOwnerId(ownerId).map { it.toResponse() }
    }

    fun updatePet(id: Long, request: PetRequest): PetResponse {
        val existing = petRepository.findById(id)
            .orElseThrow { PetNotFoundException("Pet no encontrado con id: $id") }
        val owner = ownerRepository.findById(request.ownerId)
            .orElseThrow { OwnerNotFoundException("Owner no encontrado con id: ${request.ownerId}") }
        return petRepository.save(request.toEntity(existing, owner)).toResponse()
    }

    fun deletePet(id: Long) {
        if (!petRepository.existsById(id)) {
            throw PetNotFoundException("Pet no encontrado con id: $id")
        }
        petRepository.deleteById(id)
    }
}
