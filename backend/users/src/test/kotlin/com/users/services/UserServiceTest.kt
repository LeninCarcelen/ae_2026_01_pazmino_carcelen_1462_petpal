package com.users.services

import com.users.audit.AuditService
import com.users.dto.UserRequest
import com.users.entities.User
import com.users.exceptions.CognitoSubAlreadyExistsException
import com.users.exceptions.EmailAlreadyExistsException
import com.users.exceptions.UserNotFoundException
import com.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var auditService: AuditService
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        auditService = mock()
        userService = UserService(userRepository, auditService)
    }

    @Test
    fun `createUser guarda el usuario cuando email y cognitoSub no existen`() {
        val request = UserRequest(cognitoSub = "sub-1", email = "juan@mail.com", fullName = "Juan Perez", phone = "0999999999", role = "Clients")
        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(userRepository.existsByCognitoSub(request.cognitoSub)).thenReturn(false)
        whenever(userRepository.save(any())).thenAnswer { inv ->
            val user = inv.arguments[0] as User
            User(id = 1L, cognitoSub = user.cognitoSub, email = user.email, fullName = user.fullName, phone = user.phone, role = user.role)
        }

        val response = userService.createUser(request)

        assertEquals(1L, response.id)
        assertEquals("Juan Perez", response.fullName)
        verify(userRepository).save(any())
    }

    @Test
    fun `createUser lanza EmailAlreadyExistsException si el email ya existe`() {
        val request = UserRequest(cognitoSub = "sub-1", email = "juan@mail.com", fullName = "Juan Perez", role = "Clients")
        whenever(userRepository.existsByEmail(request.email)).thenReturn(true)

        assertThrows(EmailAlreadyExistsException::class.java) {
            userService.createUser(request)
        }
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `createUser lanza CognitoSubAlreadyExistsException si el sub ya existe`() {
        val request = UserRequest(cognitoSub = "sub-1", email = "juan@mail.com", fullName = "Juan Perez", role = "Clients")
        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(userRepository.existsByCognitoSub(request.cognitoSub)).thenReturn(true)

        assertThrows(CognitoSubAlreadyExistsException::class.java) {
            userService.createUser(request)
        }
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `getUserById lanza UserNotFoundException si no existe`() {
        whenever(userRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(UserNotFoundException::class.java) {
            userService.getUserById(99L)
        }
    }

    @Test
    fun `getUserByCognitoSub retorna el usuario cuando existe`() {
        val user = User(id = 1L, cognitoSub = "sub-1", email = "ana@mail.com", fullName = "Ana", role = "Clients")
        whenever(userRepository.findByCognitoSub("sub-1")).thenReturn(Optional.of(user))

        val response = userService.getUserByCognitoSub("sub-1")

        assertEquals("Ana", response.fullName)
    }

    @Test
    fun `deleteUser lanza UserNotFoundException si no existe`() {
        whenever(userRepository.findById(5L)).thenReturn(Optional.empty())

        assertThrows(UserNotFoundException::class.java) {
            userService.deleteUser(5L)
        }
    }
}
