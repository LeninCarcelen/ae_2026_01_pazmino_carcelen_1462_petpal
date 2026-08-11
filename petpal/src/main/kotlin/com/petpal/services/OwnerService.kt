package com.petpal.services

import com.petpal.audit.AuditService
import com.petpal.dto.OwnerRequest
import com.petpal.dto.OwnerResponse
import com.petpal.exceptions.EmailAlreadyExistsException
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.logging.LogEvent
import com.petpal.mappers.toEntity
import com.petpal.mappers.toResponse
import com.petpal.repositories.OwnerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class OwnerService(
    private val ownerRepository: OwnerRepository,
    private val auditService: AuditService
) {
    private val logger = LoggerFactory.getLogger(OwnerService::class.java)

    fun createOwner(request: OwnerRequest): OwnerResponse {
        if (ownerRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException("Email already registered: ${request.email}")
        }
        val saved = ownerRepository.save(request.toEntity())
        logger.info(LogEvent.format("owner.created", "Owner created", "email" to saved.email))
        auditService.record("Owner", saved.id, "CREATE", after = saved.toResponse().toString())
        return saved.toResponse()
    }

    fun getAllOwners(): List<OwnerResponse> {
        logger.info(LogEvent.format("owner.list", "Listing all owners"))
        return ownerRepository.findAll().map { it.toResponse() }
    }

    fun getOwnerById(id: Long): OwnerResponse {
        val owner = ownerRepository.findById(id)
            .orElseThrow { OwnerNotFoundException("Owner not found with id: $id") }
        return owner.toResponse()
    }

    fun deleteOwner(id: Long) {
        val existing = ownerRepository.findById(id)
            .orElseThrow { OwnerNotFoundException("Owner not found with id: $id") }
        val before = existing.toResponse().toString()
        ownerRepository.deleteById(id)
        logger.info(LogEvent.format("owner.deleted", "Owner deleted", "id" to id))
        auditService.record("Owner", id, "DELETE", before = before)
    }
}
