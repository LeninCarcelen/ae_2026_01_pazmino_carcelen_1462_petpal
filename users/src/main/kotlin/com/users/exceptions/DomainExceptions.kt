package com.users.exceptions

class UserNotFoundException(message: String) : RuntimeException(message)

class EmailAlreadyExistsException(message: String) : RuntimeException(message)

class CognitoSubAlreadyExistsException(message: String) : RuntimeException(message)
