package com.petpal.services

import com.petpal.dto.VaccineRequest
import com.petpal.entities.Pet
import com.petpal.entities.Vaccine
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VaccineNotFoundException
import com.petpal.repositories.PetRepository
import com.petpal.repositories.VaccineRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional

class VaccineServiceTest {

    private lateinit var vaccineRepository: VaccineRepository
    private lateinit var petRepository: PetRepository
    private lateinit var vaccineService: VaccineService

    private val pet = Pet(id = 1L, name = "Firulais", species = "Perro")

    @BeforeEach
    fun setUp() {
        vaccineRepository = mock()
        petRepository = mock()
        vaccineService = VaccineService(vaccineRepository, petRepository)
    }

    @Test
    fun `createVaccine guarda la vacuna cuando el pet existe y las fechas son validas`() {
        val request = VaccineRequest(
            name = "Rabia",
            dateApplied = LocalDate.now().minusDays(1),
            nextDueDate = LocalDate.now().plusYears(1),
            petId = 1L
        )
        whenever(petRepository.findById(1L)).thenReturn(Optional.of(pet))
        whenever(vaccineRepository.save(any())).thenAnswer { inv ->
            val v = inv.arguments[0] as Vaccine
            Vaccine(id = 5L, name = v.name, dateApplied = v.dateApplied, nextDueDate = v.nextDueDate, pet = v.pet)
        }

        val response = vaccineService.createVaccine(request)

        assertEquals(5L, response.id)
        assertEquals("Firulais", response.petName)
    }

    @Test
    fun `createVaccine lanza PetNotFoundException si el pet no existe`() {
        val request = VaccineRequest(name = "Rabia", dateApplied = LocalDate.now(), petId = 99L)
        whenever(petRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(PetNotFoundException::class.java) {
            vaccineService.createVaccine(request)
        }
    }

    @Test
    fun `createVaccine lanza DomainValidationException si nextDueDate es anterior a dateApplied`() {
        val request = VaccineRequest(
            name = "Rabia",
            dateApplied = LocalDate.now(),
            nextDueDate = LocalDate.now().minusDays(5),
            petId = 1L
        )
        whenever(petRepository.findById(1L)).thenReturn(Optional.of(pet))

        assertThrows(DomainValidationException::class.java) {
            vaccineService.createVaccine(request)
        }
    }

    @Test
    fun `getVaccineById lanza VaccineNotFoundException si no existe`() {
        whenever(vaccineRepository.findById(7L)).thenReturn(Optional.empty())

        assertThrows(VaccineNotFoundException::class.java) {
            vaccineService.getVaccineById(7L)
        }
    }
}
