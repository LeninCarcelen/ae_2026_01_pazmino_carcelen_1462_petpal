package com.petpal.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.petpal.dto.VeterinarianRequest
import com.petpal.dto.VeterinarianResponse
import com.petpal.exceptions.VeterinarianNotFoundException
import com.petpal.security.CognitoJwtAuthenticationConverter
import com.petpal.security.SecurityConfig
import com.petpal.services.VeterinarianService
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

@WebMvcTest(VeterinarianController::class)
@Import(SecurityConfig::class, CognitoJwtAuthenticationConverter::class)
class VeterinarianControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var veterinarianService: VeterinarianService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `POST veterinarians retorna 201 cuando el body es valido`() {
        val request = VeterinarianRequest(name = "Dra. Lopez", specialty = "General")
        val response = VeterinarianResponse(id = 1L, name = "Dra. Lopez", specialty = "General")
        whenever(veterinarianService.createVeterinarian(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/veterinarians")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Dra. Lopez"))
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `POST veterinarians retorna 400 cuando falta la especialidad`() {
        val request = VeterinarianRequest(name = "Dra. Lopez", specialty = "")

        mockMvc.perform(
            post("/api/veterinarians")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `GET veterinarians by id retorna 404 cuando no existe`() {
        whenever(veterinarianService.getVeterinarianById(3L)).thenThrow(VeterinarianNotFoundException("Veterinario no encontrado con id: 3"))

        mockMvc.perform(get("/api/veterinarians/3"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `POST veterinarians con rol OWNER retorna 403`() {
        val request = VeterinarianRequest(name = "Dra. Lopez", specialty = "General")

        mockMvc.perform(
            post("/api/veterinarians")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `GET veterinarians con rol OWNER retorna 200`() {
        whenever(veterinarianService.getAllVeterinarians()).thenReturn(emptyList())

        mockMvc.perform(get("/api/veterinarians"))
            .andExpect(status().isOk)
    }
}
