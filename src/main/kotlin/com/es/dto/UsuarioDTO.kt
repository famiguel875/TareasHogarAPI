package com.es.dto

import java.time.LocalDateTime

data class UsuarioDTO(
    val id: String?,
    val username: String,
    val password: String,  // Idealmente se omite el password en la respuesta.
    val rol: String?,
    val email: String?,
    val fechaRegistro: LocalDateTime
)
