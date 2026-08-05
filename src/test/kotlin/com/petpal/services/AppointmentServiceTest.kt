package com.petpal.services

import com.petpal.dto.AppointmentRequest
import com.petpal.entities.Appointment
import com.petpal.entities.AppointmentStatus
import com.petpal.entities.Pet
import com.petpal.entities.Veterinarian
import com.petpal.exceptions.AppointmentNotFoundException
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.repositories.AppointmentRepository
import com.petpal.repositories.PetRepository
import com.petpal.repositories.VeterinarianRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

class AppointmentServiceTest {

    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var petRepository: PetRepository
    private lateinit var veterinarianRepository: VeterinarianRepository
    private lateinit var appointmentService: AppointmentService

    private val pet = Pet(id = 1L, name = "Firulais", species = "Perro")
    private val vet = Veterinarian(id = 1L, name = "Dra. Lopez", specialty = "General")

    @BeforeEach
    fun setUp() {
        appointmentRepository = mock()
        petRepository = mock()
        veterinarianRepository = mock()
        appointmentService = AppointmentService(appointmentRepository, petRepository, veterinarianRepository)
    }

    @Test
    fun `createAppointment guarda la cita con sus veterinarios`() {
        val request = AppointmentRequest(
            date = LocalDateTime.now().plusDays(1),
            reason = "Chequeo anual",
            petId = 1L,
            veterinarianIds = listOf(1L)
        )
        whenever(petRepository.findById(1L)).thenReturn(Optional.of(pet))
        whenever(veterinarianRepository.findById(1L)).thenReturn(Optional.of(vet))
        whenever(appointmentRepository.save(any())).thenAnswer { inv ->
            val a = inv.arguments[0] as Appointment
            Appointment(id = 20L, date = a.date, reason = a.reason, status = a.status, pet = a.pet, veterinarians = a.veterinarians)
        }

        val response = appointmentService.createAppointment(request)

        assertEquals(20L, response.id)
        assertEquals("SCHEDULED", response.status)
        assertEquals(1, response.veterinarians.size)
    }

    @Test
    fun `createAppointment lanza PetNotFoundException si el pet no existe`() {
        val request = AppointmentRequest(date = LocalDateTime.now().plusDays(1), reason = "Chequeo", petId = 99L, veterinarianIds = listOf(1L))
        whenever(petRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(PetNotFoundException::class.java) {
            appointmentService.createAppointment(request)
        }
    }

    @Test
    fun `createAppointment lanza VeterinarianNotFoundException si el veterinario no existe`() {
        val request = AppointmentRequest(date = LocalDateTime.now().plusDays(1), reason = "Chequeo", petId = 1L, veterinarianIds = listOf(99L))
        whenever(petRepository.findById(1L)).thenReturn(Optional.of(pet))
        whenever(veterinarianRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(VeterinarianNotFoundException::class.java) {
            appointmentService.createAppointment(request)
        }
    }

    @Test
    fun `updateStatus actualiza el estado cuando es valido`() {
        val existing = Appointment(id = 1L, date = LocalDateTime.now().plusDays(1), reason = "Chequeo", status = AppointmentStatus.SCHEDULED, pet = pet)
        whenever(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing))
        whenever(appointmentRepository.save(any())).thenAnswer { it.arguments[0] as Appointment }

        val response = appointmentService.updateStatus(1L, "COMPLETED")

        assertEquals("COMPLETED", response.status)
    }

    @Test
    fun `updateStatus lanza DomainValidationException si el estado no es valido`() {
        val existing = Appointment(id = 1L, date = LocalDateTime.now().plusDays(1), reason = "Chequeo", status = AppointmentStatus.SCHEDULED, pet = pet)
        whenever(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing))

        assertThrows(DomainValidationException::class.java) {
            appointmentService.updateStatus(1L, "INVALIDO")
        }
    }

    @Test
    fun `updateStatus lanza AppointmentNotFoundException si la cita no existe`() {
        whenever(appointmentRepository.findById(5L)).thenReturn(Optional.empty())

        assertThrows(AppointmentNotFoundException::class.java) {
            appointmentService.updateStatus(5L, "COMPLETED")
        }
    }
}
