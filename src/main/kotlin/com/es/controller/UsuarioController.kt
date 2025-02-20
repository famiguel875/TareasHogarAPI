package com.es.controller

import com.es.dto.LoginUsuarioDTO
import com.es.dto.UsuarioDTO
import com.es.dto.UsuarioRegisterDTO
import com.es.error.exception.NotAuthorizedException
import com.es.model.Usuario
import com.es.service.TokenService
import com.es.service.UsuarioService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var usuarioService: UsuarioService

    /**
     * Registro de usuario a partir del DTO.
     */
    @PostMapping("/register")
    fun insert(
        httpRequest: HttpServletRequest,
        @RequestBody usuarioRegisterDTO: UsuarioRegisterDTO
    ): ResponseEntity<UsuarioDTO> {
        val usuarioDTO = usuarioService.insertUser(usuarioRegisterDTO)
        return ResponseEntity(usuarioDTO, HttpStatus.CREATED)
    }

    /**
     * Login de usuario.
     * Retorna un token JWT si la autenticación es exitosa.
     */
    @PostMapping("/login")
    fun login(@RequestBody usuario: LoginUsuarioDTO): ResponseEntity<Any> {
        val authentication: Authentication = try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(usuario.username, usuario.password)
            )
        } catch (e: AuthenticationException) {
            throw NotAuthorizedException("Credenciales incorrectas")
        }
        // Si la autenticación es exitosa, generar el token JWT.
        val token = tokenService.generarToken(authentication)
        return ResponseEntity(mapOf("token" to token), HttpStatus.CREATED)
    }

    // Métodos auxiliares para validación de permisos

    private fun isAdmin(authentication: Authentication): Boolean {
        return authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    private fun isOwnerOrAdmin(authentication: Authentication, username: String): Boolean {
        return authentication.name == username || isAdmin(authentication)
    }

    // Método auxiliar para mapear la entidad Usuario a UsuarioDTO.
    private fun convertToDTO(usuario: Usuario): UsuarioDTO {
        return UsuarioDTO(
            id = usuario.id,
            username = usuario.username,
            password = usuario.password,  // Nota: Se recomienda omitir la contraseña en respuestas reales.
            rol = usuario.roles,
            email = usuario.email,
            fechaRegistro = usuario.fechaRegistro
        )
    }

    /**
     * Obtiene un usuario específico por username.
     * Se permite si el usuario autenticado es el propietario o es ADMIN.
     */
    @GetMapping("/{username}")
    fun getUsuarioByUsername(
        authentication: Authentication,
        @PathVariable username: String
    ): ResponseEntity<UsuarioDTO> {
        val usuario = usuarioService.findByUsername(username)
        if (!isOwnerOrAdmin(authentication, usuario.username)) {
            throw NotAuthorizedException("Acceso denegado para ver este usuario.")
        }
        return ResponseEntity.ok(convertToDTO(usuario))
    }

    /**
     * Obtiene todos los usuarios.
     * Sólo accesible para usuarios con rol ADMIN.
     */
    @GetMapping
    fun getAllUsuarios(authentication: Authentication): ResponseEntity<List<UsuarioDTO>> {
        if (!isAdmin(authentication)) {
            throw NotAuthorizedException("Acceso denegado para ver todos los usuarios.")
        }
        val usuariosDTO = usuarioService.getAllUsuarios().map { convertToDTO(it) }
        return ResponseEntity.ok(usuariosDTO)
    }

    /**
     * Actualiza los datos de un usuario.
     * Se permite si el usuario autenticado es el propietario o es ADMIN.
     */
    @PutMapping("/{username}")
    fun updateUsuario(
        authentication: Authentication,
        @PathVariable username: String,
        @RequestBody updatedUsuario: Usuario
    ): ResponseEntity<UsuarioDTO> {
        if (!isOwnerOrAdmin(authentication, username)) {
            throw NotAuthorizedException("Acceso denegado para actualizar este usuario.")
        }
        // Se utiliza el username de la URL para identificar el recurso a actualizar.
        val usuarioToUpdate = updatedUsuario.copy(username = username)
        val updatedEntity = usuarioService.updateUsuario(usuarioToUpdate)
        return ResponseEntity.ok(convertToDTO(updatedEntity))
    }

    /**
     * Elimina un usuario por username.
     * Sólo accesible para usuarios con rol ADMIN.
     */
    @DeleteMapping("/{username}")
    fun deleteUsuario(
        authentication: Authentication,
        @PathVariable username: String
    ): ResponseEntity<Void> {
        if (!isAdmin(authentication)) {
            throw NotAuthorizedException("Acceso denegado para eliminar este usuario.")
        }
        usuarioService.deleteUsuario(username)
        return ResponseEntity.noContent().build()
    }
}



