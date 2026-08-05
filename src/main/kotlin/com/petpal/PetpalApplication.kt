package com.petpal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PetpalApplication

fun main(args: Array<String>) {
	runApplication<PetpalApplication>(*args)
}
