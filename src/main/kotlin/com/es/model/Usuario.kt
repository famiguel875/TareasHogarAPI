package com.es.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "collUsuarios")
data class Usuario(
    @Id
    val id: String? = null,
    val username: String,
    var password: String,
    var roles: String? = null,
    var email: String? = null,
    val fechaRegistro: LocalDateTime = LocalDateTime.now()
)
