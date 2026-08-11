package com.petpal.services

import com.petpal.audit.AuditService
import com.petpal.dto.PetRequest
import com.petpal.entities.Owner
import com.petpal.entities.Pet
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.repositories.OwnerRepository
import com.petpal.repositories.PetRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional

class PetServiceTest {

    private lateinit var petRepository: PetRepository
    private lateinit var ownerRepository: OwnerRepository
    private lateinit var auditService: AuditService
    private lateinit var petService: PetService

    private val owner = Owner(id = 1L, name = "Ana", email = "ana@mail.com", phone = "0988888888")

    @BeforeEach
    fun setUp() {
        petRepository = mock()
        ownerRepository = mock()
        auditService = mock()
        petService = PetService(petRepository, ownerRepository, auditService)
    }

    @Test
    fun `createPet guarda el pet cuando el owner existe`() {
        val request = PetRequest(name = "Firulais", species = "Perro", breed = "Mestizo", birthDate = LocalDate.now().minusYears(2), ownerId = 1L)
        whenever(ownerRepository.findById(1L)).thenReturn(Optional.of(owner))
        whenever(petRepository.save(any())).thenAnswer { inv ->
            val pet = inv.arguments[0] as Pet
            Pet(id = 10L, name = pet.name, species = pet.species, breed = pet.breed, birthDate = pet.birthDate, owner = pet.owner)
        }

        val response = petService.createPet(request)

        assertEquals(10L, response.id)
        assertEquals("Firulais", response.name)
        assertEquals("Ana", response.ownerName)
    }

    @Test
    fun `createPet lanza OwnerNotFoundException si el owner no existe`() {
        val request = PetRequest(name = "Firulais", species = "Perro", ownerId = 99L)
        whenever(ownerRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(OwnerNotFoundException::class.java) {
            petService.createPet(request)
        }
        verify(petRepository, never()).save(any())
    }

    @Test
    fun `getPetById lanza PetNotFoundException si no existe`() {
        whenever(petRepository.findById(50L)).thenReturn(Optional.empty())

        assertThrows(PetNotFoundException::class.java) {
            petService.getPetById(50L)
        }
    }

    @Test
    fun `updatePet actualiza los datos existentes usando el mapper`() {
        val existing = Pet(id = 1L, name = "Viejo", species = "Perro", breed = "Mestizo", birthDate = LocalDate.now().minusYears(3), owner = owner)
        val request = PetRequest(name = "Nuevo", species = "Perro", breed = "Labrador", birthDate = LocalDate.now().minusYears(3), ownerId = 1L)
        whenever(petRepository.findById(1L)).thenReturn(Optional.of(existing))
        whenever(ownerRepository.findById(1L)).thenReturn(Optional.of(owner))
        whenever(petRepository.save(any())).thenAnswer { it.arguments[0] as Pet }

        val response = petService.updatePet(1L, request)

        assertEquals("Nuevo", response.name)
        assertEquals("Labrador", response.breed)
        assertEquals(1L, response.id)
    }
}
