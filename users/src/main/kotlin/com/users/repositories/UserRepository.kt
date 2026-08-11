package com.users.repositories

import com.users.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun existsByCognitoSub(cognitoSub: String): Boolean
    fun findByCognitoSub(cognitoSub: String): Optional<User>
}
