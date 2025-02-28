package com.es.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "collDirecciones")
data class Direccion(
    @Id
    val id: String? = null,
    val codigo: String, // Código secundario: formato DIRxxx (ej. "DIR001")
    val calle: String,
    val numero: String,
    val ciudad: String,
    val codigoPostal: String,
    val username: String // Almacena el username del propietario
)
