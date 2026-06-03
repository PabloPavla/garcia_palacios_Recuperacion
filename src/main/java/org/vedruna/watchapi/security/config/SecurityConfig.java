package org.vedruna.watchapi.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.vedruna.watchapi.security.filter.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;

/**
 * Clase de configuración principal para Spring Security.
 * 
 * Define la cadena de filtros de seguridad que se aplicarán a todas las 
 * peticiones HTTP, configurando el acceso a los endpoints, la gestión de 
 * sesiones como sin estado (stateless) y la integración del filtro JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    /** Filtro personalizado para procesar y validar JSON Web Tokens (JWT). */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** Proveedor de autenticación configurado para la carga de usuarios y codificación de contraseñas. */
    private final AuthenticationProvider authProvider;

    /**
     * Define la cadena de filtros de seguridad (SecurityFilterChain) que interceptará todas las peticiones HTTP.
     * 
     * @param http Objeto para configurar Spring Security a nivel HTTP.
     * @return La cadena de filtros de seguridad construida.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Deshabilita la protección CSRF (necesario para APIs REST sin sesiones)
                .csrf(csrf -> csrf.disable())
                // 2. Configura las reglas de autorización para las peticiones HTTP
                .authorizeHttpRequests(authReq ->
                        authReq
                                // Permite acceso sin autenticación a endpoints de registro y login
                                .requestMatchers("/auth/**").permitAll()
                                // Permite acceso sin autenticación a documentación OpenAPI/Swagger
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                // Permite acceso sin autenticación a endpoints públicos indicados en el enunciado
                                .requestMatchers("/public").permitAll()
                                .requestMatchers(HttpMethod.GET, "/titles/*/reviews").permitAll() // Obtener reseñas de un título es público
                                .requestMatchers(HttpMethod.GET, "/users/*").permitAll() // Ver perfil de otro usuario es público
                                // Cualquier otra petición requiere autenticación
                                .anyRequest().authenticated()
                )
                // 3. Configura la gestión de sesiones como STATELESS (crucial para JWT)
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. Asigna el proveedor de autenticación personalizado
                .authenticationProvider(authProvider)
                // 5. Agrega el filtro JWT antes del filtro estándar de autenticación por nombre de usuario y contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
}
