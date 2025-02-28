package com.es.controller

import com.es.dto.TareaDTO
import com.es.error.exception.ForbiddenException
import com.es.error.exception.NotAuthorizedException
import com.es.model.Tarea
import com.es.service.TareaService
import com.es.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tareas")
class TareaController {

    @Autowired
    private lateinit var tareaService: TareaService

    @Autowired
    private lateinit var usuarioService: UsuarioService

    // Permite el acceso si el usuario autenticado es el propietario (según username) o es ADMIN.
    private fun isOwnerOrAdmin(authentication: Authentication, ownerUsername: String): Boolean {
        return authentication.name == ownerUsername || authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    // Mapea la entidad Tarea a su DTO.
    private fun toDTO(tarea: Tarea): TareaDTO {
        return TareaDTO(
            id = tarea.id,
            codigo = tarea.codigo,
            titulo = tarea.titulo,
            descripcion = tarea.descripcion,
            estado = tarea.estado,
            username = tarea.username
        )
    }

    /**
     * GET /api/tareas
     * - ADMIN: devuelve todas las tareas.
     * - USER: devuelve únicamente las tareas asociadas al username autenticado.
     */
    @GetMapping
    fun getTareas(authentication: Authentication): ResponseEntity<List<TareaDTO>> {
        val tareas = if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            tareaService.getAllTareas()
        } else {
            tareaService.getTareasByUsername(authentication.name)
        }
        return ResponseEntity.ok(tareas.map { toDTO(it) })
    }

    /**
     * GET /api/tareas/{codigo}
     * Devuelve una tarea específica identificada por su código.
     */
    @GetMapping("/{codigo}")
    fun getTarea(@PathVariable codigo: String, authentication: Authentication): ResponseEntity<TareaDTO> {
        val tarea = tareaService.getTareaByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, tarea.username)) {
            throw ForbiddenException("No tienes permiso para ver esta tarea")
        }
        return ResponseEntity.ok(toDTO(tarea))
    }

    /**
     * POST /api/tareas
     * - Para USER: se asocia automáticamente al username autenticado.
     * - Para ADMIN: se permite asignar la tarea al username indicado en el DTO.
     * Se requiere que el cliente envíe manualmente el campo "codigo" en el body.
     */
    @PostMapping
    fun createTarea(@RequestBody tareaDTO: TareaDTO, authentication: Authentication): ResponseEntity<TareaDTO> {
        val owner = if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            tareaDTO.username
        } else {
            authentication.name
        }
        val tarea = Tarea(
            id = null,
            codigo = tareaDTO.codigo, // Debe enviarse manualmente y validarse en el service.
            titulo = tareaDTO.titulo,
            descripcion = tareaDTO.descripcion,
            estado = tareaDTO.estado,
            username = owner
        )
        val savedTarea = tareaService.createTarea(tarea)
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedTarea))
    }

    /**
     * PUT /api/tareas/{codigo}
     * Actualiza la información de una tarea.
     * NOTA: El campo "estado" NO se actualiza en este endpoint, ya que solo se modifica mediante el endpoint de completar.
     */
    @PutMapping("/{codigo}")
    fun updateTarea(
        @PathVariable codigo: String,
        @RequestBody tareaDTO: TareaDTO,
        authentication: Authentication
    ): ResponseEntity<TareaDTO> {
        val tareaExistente = tareaService.getTareaByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, tareaExistente.username)) {
            throw ForbiddenException("No tienes permiso para actualizar esta tarea")
        }
        // Se actualizan solo título y descripción; el estado permanece igual.
        val newOwner = if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            tareaDTO.username
        } else tareaExistente.username
        val updatedTarea = tareaExistente.copy(
            titulo = tareaDTO.titulo,
            descripcion = tareaDTO.descripcion,
            // No se modifica el estado aquí.
            username = newOwner
        )
        val savedTarea = tareaService.updateTarea(updatedTarea)
        return ResponseEntity.ok(toDTO(savedTarea))
    }

    /**
     * PUT /api/tareas/{codigo}/completar
     * Marca una tarea como completada.
     * Este endpoint es el único que puede modificar el estado, y solo permite los valores "PENDIENTE" y "COMPLETADA".
     */
    @PutMapping("/{codigo}/completar")
    fun completarTarea(@PathVariable codigo: String, authentication: Authentication): ResponseEntity<TareaDTO> {
        val tarea = tareaService.getTareaByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, tarea.username)) {
            throw ForbiddenException("No tienes permiso para completar esta tarea")
        }
        // Este endpoint fija el estado a "COMPLETADA".
        val completedTask = tareaService.completarTarea(codigo, authentication.name, authentication.authorities.any { it.authority == "ROLE_ADMIN" })
        return ResponseEntity.ok(toDTO(completedTask))
    }

    /**
     * DELETE /api/tareas/{codigo}
     * Elimina una tarea.
     */
    @DeleteMapping("/{codigo}")
    fun deleteTarea(@PathVariable codigo: String, authentication: Authentication): ResponseEntity<TareaDTO> {
        val tarea = tareaService.getTareaByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, tarea.username)) {
            throw ForbiddenException("No tienes permiso para eliminar esta tarea")
        }
        val deletedTarea = tareaService.deleteTarea(codigo, authentication.name, authentication.authorities.any { it.authority == "ROLE_ADMIN" })
        return ResponseEntity.ok(toDTO(deletedTarea))
    }
}

