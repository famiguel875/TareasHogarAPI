package com.es.service

import com.es.error.exception.BadRequestException
import com.es.error.exception.NotAuthorizedException
import com.es.error.exception.NotFoundException
import com.es.model.Tarea
import com.es.repository.TareaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TareaService {

    @Autowired
    private lateinit var tareaRepository: TareaRepository

    fun getAllTareas(): List<Tarea> = tareaRepository.findAll()

    fun getTareasByUsername(username: String): List<Tarea> = tareaRepository.findByUsername(username)

    fun getTareaByCodigo(codigo: String): Tarea {
        return tareaRepository.findByCodigo(codigo)
            ?: throw NotFoundException("Tarea con código $codigo no encontrada")
    }

    fun createTarea(tarea: Tarea): Tarea {
        // Validar que el código se proporcione manualmente y cumpla el formato "TAR" + 3 dígitos.
        if (tarea.codigo.isBlank()) {
            throw BadRequestException("El código es obligatorio y debe cumplir el formato (TARxxx)")
        }
        if (!tarea.codigo.matches(Regex("^TAR\\d{3}$"))) {
            throw BadRequestException("El código de tarea no cumple el formato requerido (TARxxx)")
        }
        // Verificar que el código sea único
        if (tareaRepository.findByCodigo(tarea.codigo) != null) {
            throw BadRequestException("El código ${tarea.codigo} ya existe. Debe ser único.")
        }
        return tareaRepository.save(tarea)
    }

    fun updateTarea(tarea: Tarea): Tarea = tareaRepository.save(tarea)

    fun completarTarea(codigo: String, username: String, isAdmin: Boolean): Tarea {
        val tarea = getTareaByCodigo(codigo)
        if (!isAdmin && tarea.username != username) {
            throw NotAuthorizedException("No tienes permiso para completar esta tarea")
        }
        tarea.estado = "COMPLETADA"
        return tareaRepository.save(tarea)
    }

    fun deleteTarea(codigo: String, username: String, isAdmin: Boolean): Tarea {
        val tarea = getTareaByCodigo(codigo)
        if (!isAdmin && tarea.username != username) {
            throw NotAuthorizedException("No tienes permiso para eliminar esta tarea")
        }
        tareaRepository.delete(tarea)
        return tarea
    }
}


