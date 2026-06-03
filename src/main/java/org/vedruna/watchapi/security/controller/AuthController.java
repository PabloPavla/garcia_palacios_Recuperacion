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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST encargado de manejar todas las peticiones relacionadas 
 * con la autenticación y gestión de usuarios (registro, login, obtener usuario actual, refresh).
 */
@CrossOrigin
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para registro, login y renovación de tokens de usuario")
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
    @Operation(summary = "Registrar usuario", description = "Permite registrar un nuevo usuario con rol USER en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada no válidos o nombre de usuario/email ya registrado")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Recibida solicitud de registro para el usuario: {}", request.getUsername());
        User registeredUser = authService.register(userConverter.registerToEntity(request));
        log.info("Usuario registrado exitosamente con ID: {}", registeredUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userConverter.toDto(registeredUser));
    }

    /**
     * Endpoint para la autenticación (inicio de sesión) de un usuario.
     * 
     * @param request El DTO que contiene las credenciales de login.
     * @return ResponseEntity que contiene el AuthResponseDTO (con el JWT y Refresh Token).
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario por nombre de usuario y contraseña y devuelve un token JWT junto a su refresh token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, tokens generados"),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Recibida solicitud de inicio de sesión para el usuario: {}", request.getUsername());
        AuthResponseDTO response = authService.login(userConverter.loginToEntity(request));
        log.info("Sesión iniciada correctamente para el usuario: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener los datos del usuario actualmente autenticado a través del token JWT.
     * 
     * @param userLogueado La entidad User del usuario autenticado, inyectada por Spring Security.
     * @return UserDTO que contiene la información pública del usuario.
     */
    @Operation(summary = "Obtener usuario actual", description = "Obtiene los datos del perfil del usuario actualmente autenticado descodificando el token JWT enviado en la cabecera.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario recuperado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (falta o expira el token JWT)")
    })
    @GetMapping(value = "/me")
    public UserDTO me(@AuthenticationPrincipal User userLogueado) {
        log.info("Recibida solicitud /auth/me para el usuario autenticado: {}", userLogueado.getUsername());
        return userConverter.toDto(userLogueado);
    }

    /**
     * Endpoint para renovar el Access Token utilizando un Refresh Token.
     *
     * @param request El cuerpo de la solicitud que contiene el Refresh Token.
     * @return ResponseEntity con el nuevo Access Token.
     */
    @Operation(summary = "Refrescar token", description = "Genera un nuevo Access Token a partir de un Refresh Token válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access Token renovado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Refresh Token inválido o expirado")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshRequestDTO request) {
        log.info("Recibida solicitud de renovación de token");
        AuthResponseDTO response = authService.refreshToken(request.getRefreshToken());
        log.info("Token renovado exitosamente");
        return ResponseEntity.ok(response);
    }
}
