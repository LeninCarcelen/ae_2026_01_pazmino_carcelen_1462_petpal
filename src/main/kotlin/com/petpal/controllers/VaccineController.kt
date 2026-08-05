package com.petpal.controllers

import com.petpal.dto.VaccineRequest
import com.petpal.dto.VaccineResponse
import com.petpal.services.VaccineService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vaccines")
class VaccineController(
    private val vaccineService: VaccineService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createVaccine(@Valid @RequestBody request: VaccineRequest): VaccineResponse =
        vaccineService.createVaccine(request)

    @GetMapping
    fun getAllVaccines(@RequestParam(required = false) petId: Long?): List<VaccineResponse> =
        if (petId != null) vaccineService.getVaccinesByPetId(petId) else vaccineService.getAllVaccines()

    @GetMapping("/{id}")
    fun getVaccineById(@PathVariable id: Long): VaccineResponse =
        vaccineService.getVaccineById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteVaccine(@PathVariable id: Long) =
        vaccineService.deleteVaccine(id)
}
