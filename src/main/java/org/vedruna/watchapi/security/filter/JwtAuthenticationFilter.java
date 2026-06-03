package org.vedruna.watchapi.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vedruna.watchapi.security.service.JWTServiceImpl;

import io.jsonwebtoken.JwtException;

import java.io.IOException;

/**
 * Filtro de seguridad que intercepta todas las peticiones para validar 
 * la presencia y validez de un JSON Web Token (JWT) de ACCESO en la cabecera Authorization.
 * Si el Access Token es válido, establece la autenticación en el SecurityContext.
 */
@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Servicio que maneja la lógica de creación, validación y extracción de datos del JWT. */
    private final JWTServiceImpl jwtService;

    /** Componente de Spring Security para cargar los detalles del usuario a partir del 'username'. */
    private final UserDetailsService userDetailsService;

    /** Prefijo de la cabecera de autorización */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Lógica principal del filtro que se ejecuta en cada solicitud.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = getTokenFromRequest(request);
        final String username;

        // 1. Verificación Inicial del Token de Acceso
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extracción del Username
            username = jwtService.getUsernameFromAccessToken(token);
        } catch (JwtException e) { 
            log.warn("Error de token JWT de Acceso (inválido/expirado) en {}: {}", request.getRequestURI(), e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) { 
            log.error("Error inesperado durante el procesamiento del token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Verificación de Autenticación
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 4. Carga de Detalles del Usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Validación Final del Access Token
            if (jwtService.isAccessTokenValid(token, userDetails)) {
                
                // 6. Creación del Objeto de Autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, 
                    userDetails.getAuthorities()
                );

                // 7. Configuración de Detalles de la Solicitud
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Establecimiento en el Contexto de Seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Usuario autenticado exitosamente: {}", username);
            }
        }

        // 9. Continuación de la Cadena
        filterChain.doFilter(request, response);
    }

    /**
     * Método auxiliar para extraer el token JWT de la cabecera 'Authorization'.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
