package org.vedruna.watchapi.security.config;

import java.util.NoSuchElementException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import lombok.AllArgsConstructor;

/**
 * Clase de configuración principal para definir los Beans relacionados 
 * con la seguridad de la aplicación (Spring Security).
 * 
 * Es responsable de configurar el gestor de autenticación, el proveedor 
 * de autenticación, el servicio de detalles de usuario y el codificador 
 * de contraseñas.
 */
@Configuration
@AllArgsConstructor
public class ApplicationConfig {

    /** Repositorio utilizado para acceder a los datos de los usuarios. */
    private final UserRepository userRepo;

    /**
     * Define el Bean de AuthenticationManager.
     * 
     * AuthenticationManager es el componente principal de Spring Security 
     * que delega la autenticación a los AuthenticationProviders.
     * 
     * @param config La configuración de autenticación proporcionada por Spring Security.
     * @return El gestor de autenticación configurado.
     * @throws Exception Si ocurre un error al obtener el gestor de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el Bean de AuthenticationProvider.
     * 
     * Configura un DaoAuthenticationProvider, que es el encargado de 
     * buscar los detalles del usuario a través del UserDetailsService 
     * y de verificar la contraseña usando el PasswordEncoder.
     * 
     * @param config La configuración de autenticación.
     * @return El proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(AuthenticationConfiguration config) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Define el Bean de UserDetailsService.
     * 
     * Contrato de Spring Security para cargar la información del usuario 
     * buscando en la base de datos por nombre de usuario.
     * 
     * @return Una implementación de UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailService() {
        return username -> userRepo.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    /**
     * Define el Bean de PasswordEncoder.
     * 
     * BCryptPasswordEncoder para codificar y verificar contraseñas.
     * 
     * @return Una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
