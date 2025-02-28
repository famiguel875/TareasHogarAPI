package com.es.controller

import com.es.dto.DireccionDTO
import com.es.error.exception.ForbiddenException
import com.es.error.exception.NotAuthorizedException
import com.es.error.exception.NotFoundException
import com.es.model.Direccion
import com.es.service.DireccionService
import com.es.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/direcciones")
class DireccionController {

    @Autowired
    private lateinit var direccionService: DireccionService

    @Autowired
    private lateinit var usuarioService: UsuarioService

    private fun isOwnerOrAdmin(authentication: Authentication, ownerUsername: String): Boolean {
        return authentication.name == ownerUsername || authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    private fun toDTO(direccion: Direccion): DireccionDTO {
        return DireccionDTO(
            id = direccion.id,
            codigo = direccion.codigo,
            calle = direccion.calle,
            numero = direccion.numero,
            ciudad = direccion.ciudad,
            codigoPostal = direccion.codigoPostal,
            username = direccion.username
        )
    }

    @GetMapping
    fun getDirecciones(authentication: Authentication): ResponseEntity<List<DireccionDTO>> {
        val direcciones = if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            direccionService.getAllDirecciones()
        } else {
            listOfNotNull(direccionService.getDireccionByUsername(authentication.name))
        }
        return ResponseEntity.ok(direcciones.map { toDTO(it) })
    }

    @GetMapping("/{codigo}")
    fun getDireccion(@PathVariable codigo: String, authentication: Authentication): ResponseEntity<DireccionDTO> {
        val direccion = direccionService.getDireccionByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, direccion.username)) {
            throw ForbiddenException("No tienes permiso para ver esta dirección")
        }
        return ResponseEntity.ok(toDTO(direccion))
    }

    @GetMapping("/usuario/{username}")
    fun getDireccionByUsuario(@PathVariable username: String, authentication: Authentication): ResponseEntity<DireccionDTO> {
        if (!isOwnerOrAdmin(authentication, username)) {
            throw ForbiddenException("No tienes permiso para ver esta dirección")
        }
        val direccion = direccionService.getDireccionByUsername(username)
            ?: throw NotFoundException("Dirección para usuario $username no encontrada")
        return ResponseEntity.ok(toDTO(direccion))
    }

    @PostMapping
    fun createDireccion(@RequestBody direccion: Direccion, authentication: Authentication): ResponseEntity<DireccionDTO> {
        val owner = if (authentication.authorities.any { it.authority == "ROLE_ADMIN" }) {
            direccion.username
        } else {
            authentication.name
        }
        val nuevaDireccion = direccion.copy(username = owner)
        val savedDireccion = direccionService.createDireccion(nuevaDireccion)
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedDireccion))
    }

    @PutMapping("/{codigo}")
    fun updateDireccion(
        @PathVariable codigo: String,
        @RequestBody direccion: Direccion,
        authentication: Authentication
    ): ResponseEntity<DireccionDTO> {
        val existingDireccion = direccionService.getDireccionByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, existingDireccion.username)) {
            throw ForbiddenException("No tienes permiso para actualizar esta dirección")
        }
        val updatedDireccion = direccion.copy(id = existingDireccion.id, codigo = codigo, username = existingDireccion.username)
        val savedDireccion = direccionService.updateDireccion(updatedDireccion)
        return ResponseEntity.ok(toDTO(savedDireccion))
    }

    @DeleteMapping("/{codigo}")
    fun deleteDireccion(@PathVariable codigo: String, authentication: Authentication): ResponseEntity<DireccionDTO> {
        val existingDireccion = direccionService.getDireccionByCodigo(codigo)
        if (!isOwnerOrAdmin(authentication, existingDireccion.username)) {
            throw ForbiddenException("No tienes permiso para eliminar esta dirección")
        }
        val deletedDireccion = direccionService.deleteDireccion(existingDireccion.id!!)
        return ResponseEntity.ok(toDTO(deletedDireccion))
    }
}
