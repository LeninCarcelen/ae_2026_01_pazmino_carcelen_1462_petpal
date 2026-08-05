package com.petpal.exceptions

class OwnerNotFoundException(message: String) : RuntimeException(message)

class PetNotFoundException(message: String) : RuntimeException(message)

class VaccineNotFoundException(message: String) : RuntimeException(message)

class AppointmentNotFoundException(message: String) : RuntimeException(message)

class VeterinarianNotFoundException(message: String) : RuntimeException(message)

class EmailAlreadyExistsException(message: String) : RuntimeException(message)

class DomainValidationException(message: String) : RuntimeException(message)
