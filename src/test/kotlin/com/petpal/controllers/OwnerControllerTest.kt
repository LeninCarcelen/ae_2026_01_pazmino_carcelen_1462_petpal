package com.petpal.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.petpal.dto.OwnerRequest
import com.petpal.dto.OwnerResponse
import com.petpal.exceptions.OwnerNotFoundException
import com.petpal.security.CognitoJwtAuthenticationConverter
import com.petpal.security.SecurityConfig
import com.petpal.services.OwnerService
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

@WebMvcTest(OwnerController::class)
@Import(SecurityConfig::class, CognitoJwtAuthenticationConverter::class)
class OwnerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var ownerService: OwnerService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `POST owners retorna 201 cuando el body es valido`() {
        val request = OwnerRequest(name = "Juan", email = "juan@mail.com", phone = "0999999999")
        val response = OwnerResponse(id = 1L, name = "Juan", email = "juan@mail.com", phone = "0999999999")
        whenever(ownerService.createOwner(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Juan"))
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `POST owners retorna 400 cuando el email es invalido`() {
        val request = OwnerRequest(name = "Juan", email = "no-es-un-email", phone = "0999999999")

        mockMvc.perform(
            post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `GET owners by id retorna 404 cuando no existe`() {
        whenever(ownerService.getOwnerById(99L)).thenThrow(OwnerNotFoundException("Owner no encontrado con id: 99"))

        mockMvc.perform(get("/api/owners/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `POST owners sin autenticacion retorna 401`() {
        val request = OwnerRequest(name = "Juan", email = "juan@mail.com", phone = "0999999999")

        mockMvc.perform(
            post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `DELETE owners con rol sin permiso retorna 403`() {
        mockMvc.perform(delete("/api/owners/1"))
            .andExpect(status().isForbidden)
    }
}
