package com.petpal.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.petpal.dto.AppointmentRequest
import com.petpal.dto.AppointmentResponse
import com.petpal.exceptions.DomainValidationException
import com.petpal.security.CognitoJwtAuthenticationConverter
import com.petpal.security.SecurityConfig
import com.petpal.services.AppointmentService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(AppointmentController::class)
@Import(SecurityConfig::class, CognitoJwtAuthenticationConverter::class)
class AppointmentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var appointmentService: AppointmentService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST appointments retorna 201 cuando el body es valido`() {
        val request = AppointmentRequest(date = LocalDateTime.now().plusDays(1), reason = "Chequeo", petId = 1L, veterinarianIds = listOf(1L))
        val response = AppointmentResponse(id = 1L, date = request.date, reason = request.reason, status = "SCHEDULED", petId = 1L, petName = "Firulais")
        whenever(appointmentService.createAppointment(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("SCHEDULED"))
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST appointments retorna 400 cuando no hay veterinarios asignados`() {
        val request = AppointmentRequest(date = LocalDateTime.now().plusDays(1), reason = "Chequeo", petId = 1L, veterinarianIds = emptyList())

        mockMvc.perform(
            post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `PATCH status retorna 400 cuando el estado no es valido`() {
        whenever(appointmentService.updateStatus(1L, "INVALIDO"))
            .thenThrow(DomainValidationException("Estado inválido: 'INVALIDO'"))

        mockMvc.perform(patch("/api/appointments/1/status").param("status", "INVALIDO"))
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Hairdresser"])
    fun `POST appointments con rol Hairdresser retorna 201 (mismos permisos que Veterinarian)`() {
        val request = AppointmentRequest(date = LocalDateTime.now().plusDays(1), reason = "Chequeo", petId = 1L, veterinarianIds = listOf(1L))
        val response = AppointmentResponse(id = 1L, date = request.date, reason = request.reason, status = "SCHEDULED", petId = 1L, petName = "Firulais")
        whenever(appointmentService.createAppointment(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `POST appointments con rol OWNER retorna 403`() {
        val request = AppointmentRequest(date = LocalDateTime.now().plusDays(1), reason = "Chequeo", petId = 1L, veterinarianIds = listOf(1L))

        mockMvc.perform(
            post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `GET appointments con rol OWNER retorna 200`() {
        whenever(appointmentService.getAllAppointments()).thenReturn(emptyList())

        mockMvc.perform(get("/api/appointments"))
            .andExpect(status().isOk)
    }
}
