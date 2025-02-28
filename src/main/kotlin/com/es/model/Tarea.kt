package com.es.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "collTareas")
data class Tarea(
    @Id
    val id: String? = null,
    val codigo: String, // Código secundario: formato TARxxx (ej. "TAR001")
    val titulo: String,
    val descripcion: String,
    var estado: String, // Ej.: "PENDIENTE" o "COMPLETADA"
    val username: String // Almacena el username del propietario
)