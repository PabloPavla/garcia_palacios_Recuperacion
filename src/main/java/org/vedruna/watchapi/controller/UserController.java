package org.vedruna.watchapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.watchapi.controller.converter.UserProfileConverter;
import org.vedruna.watchapi.controller.dto.UserProfileDTO;
import org.vedruna.watchapi.controller.dto.UsernameUpdateRequestDTO;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.controller.converter.UserConverter;
import org.vedruna.watchapi.security.controller.dto.UserDTO;
import org.vedruna.watchapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar la visualización de perfiles de usuario 
 * y la modificación de su configuración.
 */
@CrossOrigin
@RestController
@AllArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios y perfiles públicos")
public class UserController {

    private final UserService userService;
    private final UserProfileConverter userProfileConverter;
    private final UserConverter userConverter;

    /**
     * Endpoint público para obtener el perfil público de un usuario dado su nombre de usuario.
     * Incluye metadatos básicos, sus reseñas y su lista de títulos favoritos.
     * 
     * @param username Nombre del usuario.
     * @return El perfil público del usuario en formato UserProfileDTO.
     */
    @Operation(summary = "Obtener perfil de usuario", description = "Recupera la información del perfil público de un usuario por su nombre de usuario, incluyendo sus reseñas y favoritos. Endpoint público.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil recuperado con éxito"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/users/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String username) {
        log.info("Solicitud pública para obtener el perfil del usuario: {}", username);
        User user = userService.getUserByUsername(username);
        UserProfileDTO profile = userProfileConverter.toProfileDto(user);
        log.info("Perfil retornado con éxito para el usuario: {}", username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Endpoint privado para actualizar el nombre de usuario del usuario autenticado actual.
     * 
     * @param request Cuerpo de la solicitud con el nuevo nombre de usuario.
     * @param user Usuario autenticado (inyectado por Spring Security).
     * @return El usuario actualizado en formato UserDTO (sin exponer contraseñas).
     */
    @Operation(summary = "Actualizar nombre de usuario", description = "Actualiza el nombre de usuario del usuario autenticado actual. Valida que el nuevo nombre no esté en uso. Requiere autenticación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nombre de usuario actualizado con éxito"),
        @ApiResponse(responseCode = "400", description = "Nombre de usuario ya está en uso o datos no válidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PatchMapping("/users/me/username")
    public ResponseEntity<UserDTO> updateUsername(
            @Valid @RequestBody UsernameUpdateRequestDTO request,
            @AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita cambiar su nombre de usuario a '{}'", user.getUsername(), request.getUsername());
        User updatedUser = userService.updateUsername(user, request.getUsername());
        log.info("Nombre de usuario cambiado con éxito de '{}' a '{}'", user.getUsername(), request.getUsername());
        return ResponseEntity.ok(userConverter.toDto(updatedUser));
    }
}
