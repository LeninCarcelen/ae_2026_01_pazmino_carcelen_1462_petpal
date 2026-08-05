package com.petpal.controllers

import com.petpal.dto.OwnerRequest
import com.petpal.dto.OwnerResponse
import com.petpal.services.OwnerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/owners")
class OwnerController(
    private val ownerService: OwnerService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOwner(@Valid @RequestBody request: OwnerRequest): OwnerResponse =
        ownerService.createOwner(request)

    @GetMapping
    fun getAllOwners(): List<OwnerResponse> =
        ownerService.getAllOwners()

    @GetMapping("/{id}")
    fun getOwnerById(@PathVariable id: Long): OwnerResponse =
        ownerService.getOwnerById(id)

    @PutMapping("/{id}")
    fun updateOwner(
        @PathVariable id: Long,
        @Valid @RequestBody request: OwnerRequest
    ): OwnerResponse =
        ownerService.updateOwner(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOwner(@PathVariable id: Long) =
        ownerService.deleteOwner(id)
}