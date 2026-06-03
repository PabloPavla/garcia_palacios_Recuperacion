package org.vedruna.watchapi.security.service;

import java.util.NoSuchElementException;
import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vedruna.watchapi.persistance.model.Rol;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.RolRepository;
import org.vedruna.watchapi.persistance.repository.UserRepository;
import org.vedruna.watchapi.security.controller.dto.AuthResponseDTO;

import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;

/**
 * Servicio encargado de gestionar las operaciones de autenticación (login),
 * registro de usuarios y renovación de tokens.
 */
@Service
@AllArgsConstructor
public class AuthService {

    /** Repositorio para acceder y manipular los datos de la entidad User. */
    private final UserRepository userRepo;

    /** Repositorio para acceder y manipular los datos de la entidad Rol. */
    private final RolRepository rolRepo;
    
    /** Servicio para la creación y validación de JSON Web Tokens (JWT). */
    private final JWTServiceImpl jwtService;
    
    /** Componente para codificar y verificar contraseñas. */
    private final PasswordEncoder passwordEncoder;
    
    /** Componente central de Spring Security para gestionar la autenticación. */
    private final AuthenticationManager authenticationManager;

    /** Servicio para obtener detalles de usuario. */
    private final UserDetailsService userDetailsService;

    /**
     * Procesa la solicitud de inicio de sesión de un usuario.
     * 
     * @param user Objeto User que contiene el nombre de usuario y la contraseña.
     * @return AuthResponseDTO que contiene el JWT generado.
     */
    public AuthResponseDTO login(User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        
        User userEntity = userRepo.findByUsername(user.getUsername()).orElseThrow(
            () -> new NoSuchElementException("User not found")
        );

        String accessToken = jwtService.generateAccessToken(userEntity);
        return new AuthResponseDTO(accessToken, jwtService.getAccessTokenExpiresIn(), jwtService.generateRefreshToken(userEntity), null);
    }

    /**
     * Registra un nuevo usuario en la aplicación con rol USER y contraseña encriptada.
     * 
     * @param user Objeto User con los datos del nuevo usuario.
     * @return La entidad User guardada en la base de datos.
     */
    public User register(User user) {
        Rol rol = rolRepo.findByRolName("USER").orElseThrow(
            () -> new NoSuchElementException("Rol not found")
        );

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateDate(LocalDate.now());
        user.setUserRol(rol);
        
        return userRepo.save(user);
    }

    /**
     * Procesa la renovación de tokens utilizando el Refresh Token proporcionado.
     *
     * @param refreshToken El Refresh Token recibido del cliente.
     * @return AuthResponseDTO con el nuevo Access Token.
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        final String username;
        try {
            username = jwtService.getUsernameFromRefreshToken(refreshToken);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Refresh Token inválido: " + e.getMessage());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Refresh Token expirado o no válido para el usuario.");
        }

        User userEntity = (User) userDetails;
        
        String newAccessToken = jwtService.generateAccessToken(userEntity);

        return AuthResponseDTO.builder()
            .accessToken(newAccessToken)
            .expiresIn(jwtService.getAccessTokenExpiresIn())
            .refreshToken(refreshToken)
            .build();
    }
}
