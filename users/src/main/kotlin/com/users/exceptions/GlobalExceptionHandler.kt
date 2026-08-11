package com.users.exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFound(ex: UserNotFoundException): Map<String, Any> =
        errorMap(404, "Not Found", ex.message)

    @ExceptionHandler(EmailAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleEmailAlreadyExists(ex: EmailAlreadyExistsException): Map<String, Any> =
        errorMap(400, "Bad Request", ex.message)

    @ExceptionHandler(CognitoSubAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleCognitoSubAlreadyExists(ex: CognitoSubAlreadyExistsException): Map<String, Any> =
        errorMap(400, "Bad Request", ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: MethodArgumentNotValidException): Map<String, Any> {
        val details = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return errorMap(400, "Bad Request", details)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(ex: ConstraintViolationException): Map<String, Any> {
        val details = ex.constraintViolations.joinToString(", ") { "${it.propertyPath}: ${it.message}" }
        return errorMap(400, "Bad Request", details)
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDenied(ex: AccessDeniedException): Map<String, Any> =
        errorMap(403, "Forbidden", "You do not have permission to perform this operation")

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneric(ex: Exception): Map<String, Any> =
        errorMap(500, "Internal Server Error", ex.message ?: "Internal server error")

    private fun errorMap(status: Int, error: String, message: String?): Map<String, Any> =
        mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to status,
            "error" to error,
            "message" to (message ?: "Error")
        )
}
