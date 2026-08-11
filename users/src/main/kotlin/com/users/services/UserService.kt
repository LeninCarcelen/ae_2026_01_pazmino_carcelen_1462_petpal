package com.users.services

import com.users.audit.AuditService
import com.users.dto.UserRequest
import com.users.dto.UserResponse
import com.users.exceptions.CognitoSubAlreadyExistsException
import com.users.exceptions.EmailAlreadyExistsException
import com.users.exceptions.UserNotFoundException
import com.users.logging.LogEvent
import com.users.mappers.copyWith
import com.users.mappers.toEntity
import com.users.mappers.toResponse
import com.users.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val auditService: AuditService
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun createUser(request: UserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException("Email already registered: ${request.email}")
        }
        if (userRepository.existsByCognitoSub(request.cognitoSub)) {
            throw CognitoSubAlreadyExistsException("cognitoSub already registered: ${request.cognitoSub}")
        }

        val saved = userRepository.save(request.toEntity())
        logger.info(LogEvent.format("user.created", "User profile created", "email" to saved.email, "role" to saved.role))
        auditService.record("User", saved.id, "CREATE", after = saved.toResponse().toString())
        return saved.toResponse()
    }

    fun getAllUsers(): List<UserResponse> {
        logger.info(LogEvent.format("user.list", "Listing all users"))
        return userRepository.findAll().map { it.toResponse() }
    }

    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }
        return user.toResponse()
    }

    fun getUserByCognitoSub(cognitoSub: String): UserResponse {
        val user = userRepository.findByCognitoSub(cognitoSub)
            .orElseThrow { UserNotFoundException("User not found for cognitoSub: $cognitoSub") }
        return user.toResponse()
    }

    fun updateUser(id: Long, request: UserRequest): UserResponse {
        val existing = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }
        val before = existing.toResponse().toString()

        val saved = userRepository.save(
            existing.copyWith(fullName = request.fullName, phone = request.phone, role = request.role)
        )
        logger.info(LogEvent.format("user.updated", "User profile updated", "id" to id))
        auditService.record("User", id, "UPDATE", before = before, after = saved.toResponse().toString())
        return saved.toResponse()
    }

    fun deleteUser(id: Long) {
        val existing = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }
        val before = existing.toResponse().toString()
        userRepository.deleteById(id)
        logger.info(LogEvent.format("user.deleted", "User profile deleted", "id" to id))
        auditService.record("User", id, "DELETE", before = before)
    }
}
