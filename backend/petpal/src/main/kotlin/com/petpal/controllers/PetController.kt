package com.petpal.controllers

import com.petpal.dto.PetRequest
import com.petpal.dto.PetResponse
import com.petpal.services.PetService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pets")
class PetController(
    private val petService: PetService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPet(@Valid @RequestBody request: PetRequest): PetResponse =
        petService.createPet(request)

    @GetMapping
    fun getAllPets(@RequestParam(required = false) ownerId: Long?): List<PetResponse> =
        if (ownerId != null) petService.getPetsByOwnerId(ownerId) else petService.getAllPets()

    @GetMapping("/{id}")
    fun getPetById(@PathVariable id: Long): PetResponse =
        petService.getPetById(id)

    @PutMapping("/{id}")
    fun updatePet(@PathVariable id: Long, @Valid @RequestBody request: PetRequest): PetResponse =
        petService.updatePet(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePet(@PathVariable id: Long) =
        petService.deletePet(id)
}
