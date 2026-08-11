package com.users.controllers

import com.users.dto.UserRequest
import com.users.dto.UserResponse
import com.users.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody request: UserRequest): UserResponse =
        userService.createUser(request)

    @GetMapping
    fun getAllUsers(): List<UserResponse> =
        userService.getAllUsers()

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal jwt: Jwt): UserResponse =
        userService.getUserByCognitoSub(jwt.subject)

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse =
        userService.getUserById(id)

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @Valid @RequestBody request: UserRequest): UserResponse =
        userService.updateUser(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) =
        userService.deleteUser(id)
}
