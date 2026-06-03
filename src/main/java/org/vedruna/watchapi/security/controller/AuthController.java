package org.vedruna.watchapi.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.controller.converter.UserConverter;
import org.vedruna.watchapi.security.controller.dto.AuthResponseDTO;
import org.vedruna.watchapi.security.controller.dto.LoginRequestDTO;
import org.vedruna.watchapi.security.controller.dto.RefreshRequestDTO;
import org.vedruna.watchapi.security.controller.dto.RegisterRequestDTO;
import org.vedruna.watchapi.security.controller.dto.UserDTO;
import org.vedruna.watchapi.security.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * Controlador REST encargado de manejar todas las peticiones relacionadas 
 * con la autenticación y gestión de usuarios (registro, login, obtener usuario actual, refresh).
 */
@CrossOrigin
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    
    /** Servicio de lógica de negocio para las operaciones de autenticación. */
    private final AuthService authService;

    /** Componente para convertir entre entidades User y DTOs. */
    private final UserConverter userConverter;

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     * 
     * @param request El DTO que contiene los datos de registro (nombre, contraseña, email, etc.).
     * @return ResponseEntity que contiene el UserDTO del usuario registrado.
     */
    @PostMapping(value = "/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            userConverter.toDto(  
                authService.register(
                    userConverter.registerToEntity(request)
                )
            )
        );
    }

    /**
     * Endpoint para la autenticación (inicio de sesión) de un usuario.
     * 
     * @param request El DTO que contiene las credenciales de login.
     * @return ResponseEntity que contiene el AuthResponseDTO (con el JWT y Refresh Token).
     */
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(
            authService.login(
                userConverter.loginToEntity(request)
            )
        );
    }

    /**
     * Endpoint para obtener los datos del usuario actualmente autenticado a través del token JWT.
     * 
     * @param userLogueado La entidad User del usuario autenticado, inyectada por Spring Security.
     * @return UserDTO que contiene la información pública del usuario.
     */
    @GetMapping(value = "/me")
    public UserDTO me(@AuthenticationPrincipal User userLogueado) {
        return userConverter.toDto(userLogueado);
    }

    /**
     * Endpoint para renovar el Access Token utilizando un Refresh Token.
     *
     * @param request El cuerpo de la solicitud que contiene el Refresh Token.
     * @return ResponseEntity con el nuevo Access Token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
}
