package com.petpal.services

import com.petpal.dto.OwnerRequest
import com.petpal.entities.Owner
import com.petpal.exceptions.EmailAlreadyExistsException
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.repositories.OwnerRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.Optional

class OwnerServiceTest {

    private lateinit var ownerRepository: OwnerRepository
    private lateinit var ownerService: OwnerService

    @BeforeEach
    fun setUp() {
        ownerRepository = mock(OwnerRepository::class.java)
        ownerService = OwnerService(ownerRepository)
    }

    @Test
    fun `createOwner guarda un owner nuevo cuando el email no existe`() {
        val request = OwnerRequest(name = "Juan Perez", email = "juan@mail.com", phone = "0999999999")
        whenever(ownerRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(ownerRepository.save(any())).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            Owner(id = 1L, name = owner.name, email = owner.email, phone = owner.phone)
        }

        val response = ownerService.createOwner(request)

        assertEquals(1L, response.id)
        assertEquals("Juan Perez", response.name)
        verify(ownerRepository).save(any())
    }

    @Test
    fun `createOwner lanza EmailAlreadyExistsException si el email ya existe`() {
        val request = OwnerRequest(name = "Juan Perez", email = "juan@mail.com", phone = "0999999999")
        whenever(ownerRepository.existsByEmail(request.email)).thenReturn(true)

        assertThrows(EmailAlreadyExistsException::class.java) {
            ownerService.createOwner(request)
        }
        verify(ownerRepository, never()).save(any())
    }

    @Test
    fun `getOwnerById lanza OwnerNotFoundException si no existe`() {
        whenever(ownerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(OwnerNotFoundException::class.java) {
            ownerService.getOwnerById(99L)
        }
    }

    @Test
    fun `getOwnerById retorna el owner cuando existe`() {
        val owner = Owner(id = 1L, name = "Ana", email = "ana@mail.com", phone = "0988888888")
        whenever(ownerRepository.findById(1L)).thenReturn(Optional.of(owner))

        val response = ownerService.getOwnerById(1L)

        assertEquals("Ana", response.name)
    }

    @Test
    fun `deleteOwner lanza OwnerNotFoundException si no existe`() {
        whenever(ownerRepository.existsById(5L)).thenReturn(false)

        assertThrows(OwnerNotFoundException::class.java) {
            ownerService.deleteOwner(5L)
        }
    }
}
