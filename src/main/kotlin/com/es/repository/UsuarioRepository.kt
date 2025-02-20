package com.es.repository

import com.es.model.Usuario
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsuarioRepository : MongoRepository<Usuario, Long> {
    fun findByUsername(username: String): Optional<Usuario>
    fun findByEmail(email: String): Optional<Usuario>
}