package com.petpal.controllers

import com.petpal.dto.AppointmentRequest
import com.petpal.dto.AppointmentResponse
import com.petpal.services.AppointmentService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/api/appointments")
class AppointmentController(
    private val appointmentService: AppointmentService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAppointment(@Valid @RequestBody request: AppointmentRequest): AppointmentResponse =
        appointmentService.createAppointment(request)

    @GetMapping
    fun getAllAppointments(@RequestParam(required = false) petId: Long?): List<AppointmentResponse> =
        if (petId != null) appointmentService.getAppointmentsByPetId(petId) else appointmentService.getAllAppointments()

    @GetMapping("/{id}")
    fun getAppointmentById(@PathVariable id: Long): AppointmentResponse =
        appointmentService.getAppointmentById(id)

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestParam @NotBlank status: String): AppointmentResponse =
        appointmentService.updateStatus(id, status)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAppointment(@PathVariable id: Long) =
        appointmentService.deleteAppointment(id)
}
