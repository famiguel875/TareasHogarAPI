package com.es.service

import com.es.error.exception.BadRequestException
import com.es.error.exception.NotFoundException
import com.es.model.Direccion
import com.es.repository.DireccionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DireccionService {

    @Autowired
    private lateinit var direccionRepository: DireccionRepository

    fun getAllDirecciones(): List<Direccion> = direccionRepository.findAll()

    fun getDireccionById(id: String): Direccion {
        return direccionRepository.findById(id)
            .orElseThrow { NotFoundException("Dirección con id $id no encontrada") }
    }

    fun getDireccionByCodigo(codigo: String): Direccion {
        return direccionRepository.findByCodigo(codigo)
            ?: throw NotFoundException("Dirección con código $codigo no encontrada")
    }

    fun getDireccionByUsername(username: String): Direccion? {
        val direcciones = direccionRepository.findAllByUsername(username)
        if (direcciones.size > 1) {
            throw BadRequestException("Se encontraron múltiples direcciones para el usuario $username")
        }
        return direcciones.firstOrNull()
    }

    fun createDireccion(direccion: Direccion): Direccion {
        // Validar que el código se proporcione manualmente y cumpla el formato "DIR" + 3 dígitos.
        if (direccion.codigo.isBlank()) {
            throw BadRequestException("El código es obligatorio y debe cumplir el formato (DIRxxx)")
        }
        if (!direccion.codigo.matches(Regex("^DIR\\d{3}$"))) {
            throw BadRequestException("El código de dirección no cumple el formato requerido (DIRxxx)")
        }
        // Verificar que el código sea único
        if (direccionRepository.findByCodigo(direccion.codigo) != null) {
            throw BadRequestException("El código ${direccion.codigo} ya existe. Debe ser único.")
        }
        return direccionRepository.save(direccion)
    }

    fun updateDireccion(direccion: Direccion): Direccion = direccionRepository.save(direccion)

    fun deleteDireccion(id: String): Direccion {
        val direccion = getDireccionById(id)
        direccionRepository.delete(direccion)
        return direccion
    }
}

