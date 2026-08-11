package com.petpal.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.petpal.dto.PetRequest
import com.petpal.dto.PetResponse
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.exceptions.PetNotFoundException
import com.petpal.logging.RequestLoggingFilter
import com.petpal.security.CognitoJwtAuthenticationConverter
import com.petpal.security.SecurityConfig
import com.petpal.services.PetService
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

@WebMvcTest(PetController::class)
@Import(SecurityConfig::class, CognitoJwtAuthenticationConverter::class, RequestLoggingFilter::class)
class PetControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var petService: PetService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST pets retorna 201 cuando el body es valido`() {
        val request = PetRequest(name = "Firulais", species = "Perro", breed = "Mestizo", birthDate = LocalDate.now().minusYears(2), ownerId = 1L)
        val response = PetResponse(id = 1L, name = "Firulais", species = "Perro", breed = "Mestizo", birthDate = request.birthDate, ownerId = 1L, ownerName = "Ana")
        whenever(petService.createPet(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Firulais"))
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST pets retorna 400 cuando falta el nombre`() {
        val request = PetRequest(name = "", species = "Perro", ownerId = 1L)

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST pets retorna 400 cuando la fecha de nacimiento es futura`() {
        val request = PetRequest(name = "Firulais", species = "Perro", birthDate = LocalDate.now().plusDays(10), ownerId = 1L)

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Veterinarian"])
    fun `POST pets retorna 404 cuando el owner no existe`() {
        val request = PetRequest(name = "Firulais", species = "Perro", ownerId = 99L)
        whenever(petService.createPet(request)).thenThrow(OwnerNotFoundException("Owner no encontrado con id: 99"))

        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `GET pets by id retorna 404 cuando no existe`() {
        whenever(petService.getPetById(50L)).thenThrow(PetNotFoundException("Pet no encontrado con id: 50"))

        mockMvc.perform(get("/api/pets/50"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `PUT pets con rol OWNER retorna 403`() {
        val request = PetRequest(name = "Firulais", species = "Perro", ownerId = 1L)

        mockMvc.perform(
            put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET pets sin autenticacion retorna 401`() {
        mockMvc.perform(get("/api/pets"))
            .andExpect(status().isUnauthorized)
    }
}
