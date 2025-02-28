package com.es.repository

import com.es.model.Direccion
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DireccionRepository : MongoRepository<Direccion, String> {
    fun findAllByUsername(username: String): List<Direccion>
    fun findByCodigo(codigo: String): Direccion?
}