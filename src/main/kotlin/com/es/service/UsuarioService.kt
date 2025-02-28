package com.es.service

import com.es.dto.UsuarioDTO
import com.es.dto.UsuarioRegisterDTO
import com.es.error.exception.BadRequestException
import com.es.error.exception.NotFoundException
import com.es.model.Usuario
import com.es.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UsuarioService(
    // Si se necesita para validar otros datos (por ejemplo, dirección), se inyecta:
    // private val externalApiService: ExternalApiService,
    @Autowired private val usuarioRepository: UsuarioRepository,
    @Autowired private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val usuario: Usuario = usuarioRepository.findByUsername(username!!)
            .orElseThrow { NotFoundException("Usuario con username $username no encontrado") }
        // Separamos roles si en la entidad se guardan como cadena separada por comas.
        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(*usuario.roles!!.split(",").toTypedArray())
            .build()
    }

    fun insertUser(usuarioInsertadoDTO: UsuarioRegisterDTO): UsuarioDTO {
        // Validar que las contraseñas coincidan
        if (usuarioInsertadoDTO.password != usuarioInsertadoDTO.passwordRepeat) {
            throw BadRequestException("Las contraseñas no coinciden")
        }

        // Comprobar si el usuario ya existe
        if (usuarioRepository.findByUsername(usuarioInsertadoDTO.username).isPresent) {
            throw BadRequestException("El usuario ya existe")
        }

        // Crear la entidad Usuario, codificando la contraseña y asignando rol por defecto si es necesario.
        val usuario = Usuario(
            id = null,
            username = usuarioInsertadoDTO.username,
            email = usuarioInsertadoDTO.email,
            password = passwordEncoder.encode(usuarioInsertadoDTO.password),
            roles = usuarioInsertadoDTO.rol ?: "USER",
            fechaRegistro = LocalDateTime.now()
        )
        usuarioRepository.save(usuario)

        // Devolver el DTO de usuario (se recomienda omitir la contraseña en la respuesta real)
        return UsuarioDTO(
            id = usuario.id,
            username = usuario.username,
            password = usuario.password,
            rol = usuario.roles,
            email = usuario.email,
            fechaRegistro = usuario.fechaRegistro
        )
    }

    fun getAllUsuarios(): List<Usuario> {
        return usuarioRepository.findAll()
    }

    fun updateUsuario(usuario: Usuario): Usuario {
        val usuarioExistente = usuarioRepository.findByUsername(usuario.username)
            .orElseThrow { NotFoundException("Usuario con username ${usuario.username} no encontrado") }
        // Actualizar únicamente email y contraseña (si se proporciona)
        usuarioExistente.email = usuario.email
        if (!usuario.password.isNullOrBlank()) {
            usuarioExistente.password = passwordEncoder.encode(usuario.password)
        }
        return usuarioRepository.save(usuarioExistente)
    }

    fun deleteUsuario(username: String): Usuario {
        val usuario = usuarioRepository.findByUsername(username)
            .orElseThrow { NotFoundException("Usuario con username $username no encontrado") }
        usuarioRepository.delete(usuario)
        return usuario
    }

    fun findByUsername(username: String): Usuario {
        return usuarioRepository.findByUsername(username)
            .orElseThrow { NotFoundException("Usuario con username $username no encontrado") }
    }
}
