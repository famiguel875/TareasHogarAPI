package com.es.repository

import com.es.model.Tarea
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TareaRepository : MongoRepository<Tarea, String> {
    fun findByUsername(username: String): List<Tarea>
    fun findByCodigo(codigo: String): Tarea?
}