package com.petpal.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.petpal.dto.VaccineRequest
import com.petpal.dto.VaccineResponse
import com.petpal.exceptions.DomainValidationException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.exceptions.VaccineNotFoundException
import com.petpal.security.CognitoJwtAuthenticationConverter
import com.petpal.security.SecurityConfig
import com.petpal.services.VaccineService
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
import java.time.LocalDate

@WebMvcTest(VaccineController::class)
@Import(SecurityConfig::class, CognitoJwtAuthenticationConverter::class)
class VaccineControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var vaccineService: VaccineService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST vaccines retorna 201 cuando el body es valido`() {
        val request = VaccineRequest(name = "Rabia", dateApplied = LocalDate.now(), nextDueDate = LocalDate.now().plusYears(1), petId = 1L)
        val response = VaccineResponse(id = 1L, name = "Rabia", dateApplied = request.dateApplied, nextDueDate = request.nextDueDate, petId = 1L, petName = "Firulais")
        whenever(vaccineService.createVaccine(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/vaccines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Rabia"))
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST vaccines retorna 400 cuando falta dateApplied`() {
        val request = VaccineRequest(name = "Rabia", dateApplied = null, petId = 1L)

        mockMvc.perform(
            post("/api/vaccines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST vaccines retorna 400 cuando nextDueDate es anterior a dateApplied`() {
        val request = VaccineRequest(name = "Rabia", dateApplied = LocalDate.now(), nextDueDate = LocalDate.now().minusDays(3), petId = 1L)
        whenever(vaccineService.createVaccine(request))
            .thenThrow(DomainValidationException("La próxima fecha de refuerzo no puede ser anterior a la fecha de aplicación"))

        mockMvc.perform(
            post("/api/vaccines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST vaccines retorna 404 cuando el pet no existe`() {
        val request = VaccineRequest(name = "Rabia", dateApplied = LocalDate.now(), petId = 99L)
        whenever(vaccineService.createVaccine(request)).thenThrow(PetNotFoundException("Pet no encontrado con id: 99"))

        mockMvc.perform(
            post("/api/vaccines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `GET vaccines by id retorna 404 cuando no existe`() {
        whenever(vaccineService.getVaccineById(7L)).thenThrow(VaccineNotFoundException("Vacuna no encontrada con id: 7"))

        mockMvc.perform(get("/api/vaccines/7"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `DELETE vaccines con rol VET retorna 403`() {
        mockMvc.perform(delete("/api/vaccines/1"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `DELETE vaccines con rol ADMIN retorna 204`() {
        mockMvc.perform(delete("/api/vaccines/1"))
            .andExpect(status().isNoContent)
    }
}
