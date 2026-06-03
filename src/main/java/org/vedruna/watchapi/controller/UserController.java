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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * Controlador REST para gestionar la visualización de perfiles de usuario 
 * y la modificación de su configuración.
 */
@CrossOrigin
@RestController
@AllArgsConstructor
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
    @GetMapping("/users/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(userProfileConverter.toProfileDto(user));
    }

    /**
     * Endpoint privado para actualizar el nombre de usuario del usuario autenticado actual.
     * 
     * @param request Cuerpo de la solicitud con el nuevo nombre de usuario.
     * @param user Usuario autenticado (inyectado por Spring Security).
     * @return El usuario actualizado en formato UserDTO (sin exponer contraseñas).
     */
    @PatchMapping("/users/me/username")
    public ResponseEntity<UserDTO> updateUsername(
            @Valid @RequestBody UsernameUpdateRequestDTO request,
            @AuthenticationPrincipal User user) {
        User updatedUser = userService.updateUsername(user, request.getUsername());
        return ResponseEntity.ok(userConverter.toDto(updatedUser));
    }
}
