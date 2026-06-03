package org.vedruna.watchapi.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vedruna.watchapi.persistance.model.Rol;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.RolRepository;
import org.vedruna.watchapi.persistance.repository.UserRepository;
import org.vedruna.watchapi.security.controller.dto.AuthResponseDTO;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private JWTServiceImpl jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Rol rol;

    @BeforeEach
    public void setUp() {
        rol = new Rol();
        rol.setRolId(1);
        rol.setRolName("USER");

        user = new User();
        user.setUserId(10);
        user.setUsername("test_user");
        user.setPassword("raw_password");
        user.setUserRol(rol);
    }

    @Test
    public void login_Success() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access_token");
        when(jwtService.getAccessTokenExpiresIn()).thenReturn(180L);
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh_token");

        AuthResponseDTO response = authService.login(user);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(user))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    public void register_Success() {
        when(rolRepository.findByRolName("USER")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register(user);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo("encoded_password");
        assertThat(result.getUserRol()).isEqualTo(rol);
        assertThat(result.getCreateDate()).isNotNull();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void register_RolNotFound_ThrowsException() {
        when(rolRepository.findByRolName("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(user))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Rol not found");
    }

    @Test
    public void refreshToken_Success() {
        when(jwtService.getUsernameFromRefreshToken("valid_refresh")).thenReturn("test_user");
        when(userDetailsService.loadUserByUsername("test_user")).thenReturn(user);
        when(jwtService.isRefreshTokenValid("valid_refresh", user)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("new_access_token");
        when(jwtService.getAccessTokenExpiresIn()).thenReturn(180L);

        AuthResponseDTO response = authService.refreshToken("valid_refresh");

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new_access_token");
        assertThat(response.getRefreshToken()).isEqualTo("valid_refresh");
    }

    @Test
    public void refreshToken_InvalidTokenFormat_ThrowsException() {
        when(jwtService.getUsernameFromRefreshToken("invalid_refresh")).thenThrow(new io.jsonwebtoken.JwtException("Invalid token"));

        assertThatThrownBy(() -> authService.refreshToken("invalid_refresh"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Refresh Token inválido");
    }

    @Test
    public void refreshToken_InvalidOrExpired_ThrowsException() {
        when(jwtService.getUsernameFromRefreshToken("expired_refresh")).thenReturn("test_user");
        when(userDetailsService.loadUserByUsername("test_user")).thenReturn(user);
        when(jwtService.isRefreshTokenValid("expired_refresh", user)).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken("expired_refresh"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Refresh Token expirado o no válido para el usuario.");
    }
}
