package com.users.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.users.dto.UserRequest
import com.users.dto.UserResponse
import com.users.exceptions.UserNotFoundException
import com.users.logging.RequestLoggingFilter
import com.users.security.CognitoJwtAuthenticationConverter
import com.users.security.SecurityConfig
import com.users.services.UserService
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

@WebMvcTest(UserController::class)
@Import(SecurityConfig::class, CognitoJwtAuthenticationConverter::class, RequestLoggingFilter::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `POST users crea un usuario y retorna 201`() {
        val request = UserRequest(cognitoSub = "sub-1", email = "juan@mail.com", fullName = "Juan Perez", phone = "0999999999", role = "Clients")
        val response = UserResponse(id = 1L, cognitoSub = "sub-1", email = "juan@mail.com", fullName = "Juan Perez", phone = "0999999999", role = "Clients", createdAt = "2026-08-07T00:00:00Z")
        whenever(userService.createUser(request)).thenReturn(response)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.fullName").value("Juan Perez"))
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `GET users retorna 403 para un rol sin permiso de listar`() {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(authorities = ["Administrator"])
    fun `GET users by id retorna 404 cuando no existe`() {
        whenever(userService.getUserById(99L)).thenThrow(UserNotFoundException("User not found with id: 99"))

        mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(authorities = ["Clients"])
    fun `DELETE users retorna 403 para un rol sin permiso de administrador`() {
        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isForbidden)
    }
}
