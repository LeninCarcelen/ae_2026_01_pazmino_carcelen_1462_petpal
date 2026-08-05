package com.petpal.services

import com.petpal.dto.VeterinarianRequest
import com.petpal.entities.Veterinarian
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.repositories.VeterinarianRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Optional

class VeterinarianServiceTest {

    private lateinit var veterinarianRepository: VeterinarianRepository
    private lateinit var veterinarianService: VeterinarianService

    @BeforeEach
    fun setUp() {
        veterinarianRepository = mock()
        veterinarianService = VeterinarianService(veterinarianRepository)
    }

    @Test
    fun `createVeterinarian guarda el veterinario`() {
        val request = VeterinarianRequest(name = "Dra. Lopez", specialty = "General")
        whenever(veterinarianRepository.save(any())).thenAnswer { inv ->
            val v = inv.arguments[0] as Veterinarian
            Veterinarian(id = 1L, name = v.name, specialty = v.specialty)
        }

        val response = veterinarianService.createVeterinarian(request)

        assertEquals(1L, response.id)
        assertEquals("Dra. Lopez", response.name)
    }

    @Test
    fun `getVeterinarianById lanza VeterinarianNotFoundException si no existe`() {
        whenever(veterinarianRepository.findById(3L)).thenReturn(Optional.empty())

        assertThrows(VeterinarianNotFoundException::class.java) {
            veterinarianService.getVeterinarianById(3L)
        }
    }

    @Test
    fun `deleteVeterinarian lanza VeterinarianNotFoundException si no existe`() {
        whenever(veterinarianRepository.existsById(9L)).thenReturn(false)

        assertThrows(VeterinarianNotFoundException::class.java) {
            veterinarianService.deleteVeterinarian(9L)
        }
    }

    @Test
    fun `deleteVeterinarian elimina cuando existe`() {
        whenever(veterinarianRepository.existsById(1L)).thenReturn(true)

        veterinarianService.deleteVeterinarian(1L)

        org.mockito.kotlin.verify(veterinarianRepository).deleteById(1L)
    }
}
