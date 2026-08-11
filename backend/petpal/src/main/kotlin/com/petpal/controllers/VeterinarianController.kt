package com.petpal.controllers

import com.petpal.dto.VeterinarianRequest
import com.petpal.dto.VeterinarianResponse
import com.petpal.services.VeterinarianService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/veterinarians")
class VeterinarianController(
    private val veterinarianService: VeterinarianService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createVeterinarian(@Valid @RequestBody request: VeterinarianRequest): VeterinarianResponse =
        veterinarianService.createVeterinarian(request)

    @GetMapping
    fun getAllVeterinarians(): List<VeterinarianResponse> =
        veterinarianService.getAllVeterinarians()

    @GetMapping("/{id}")
    fun getVeterinarianById(@PathVariable id: Long): VeterinarianResponse =
        veterinarianService.getVeterinarianById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVeterinarian(@PathVariable id: Long) =
        veterinarianService.deleteVeterinarian(id)
}
