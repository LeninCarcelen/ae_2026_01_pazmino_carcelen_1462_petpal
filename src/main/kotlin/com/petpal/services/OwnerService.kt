package com.petpal.services

import com.petpal.dto.OwnerRequest
import com.petpal.dto.OwnerResponse
import com.petpal.exceptions.EmailAlreadyExistsException
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.mappers.updateFrom
import com.petpal.repositories.OwnerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OwnerService(
    private val ownerRepository: OwnerRepository
) {
    private val logger = LoggerFactory.getLogger(OwnerService::class.java)

    fun createOwner(request: OwnerRequest): OwnerResponse {
        logger.info("Creando owner con email: ${request.email}")
        if (ownerRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException("El email ya existe: ${request.email}")
        }
        return ownerRepository.save(request.toEntity()).toResponse()
    }

    fun getAllOwners(): List<OwnerResponse> {
        logger.info("Obteniendo todos los owners")
        return ownerRepository.findAll().map { it.toResponse() }
    }

    fun getOwnerById(id: Long): OwnerResponse {
        val owner = ownerRepository.findById(id)
            .orElseThrow { OwnerNotFoundException("Owner no encontrado con id: $id") }
        return owner.toResponse()
    }

    fun updateOwner(id: Long, request: OwnerRequest): OwnerResponse {
        logger.info("Actualizando owner con id: $id")
        val owner = ownerRepository.findById(id)
            .orElseThrow { OwnerNotFoundException("Owner no encontrado con id: $id") }

        if (request.email != owner.email && ownerRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException("El email ya existe: ${request.email}")
        }

        val updatedOwner = owner.updateFrom(request)
        return ownerRepository.save(updatedOwner).toResponse()
    }

    fun deleteOwner(id: Long) {
        if (!ownerRepository.existsById(id)) {
            throw OwnerNotFoundException("Owner no encontrado con id: $id")
        }
        ownerRepository.deleteById(id)
    }
}