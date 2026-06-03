package org.vedruna.watchapi.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.config.ApplicationConfig;
import org.vedruna.watchapi.security.config.SecurityConfig;
import org.vedruna.watchapi.security.controller.converter.UserConverter;
import org.vedruna.watchapi.security.controller.dto.AuthResponseDTO;
import org.vedruna.watchapi.security.controller.dto.LoginRequestDTO;
import org.vedruna.watchapi.security.controller.dto.RefreshRequestDTO;
import org.vedruna.watchapi.security.controller.dto.RegisterRequestDTO;
import org.vedruna.watchapi.security.controller.dto.UserDTO;
import org.vedruna.watchapi.security.filter.JwtAuthenticationFilter;
import org.vedruna.watchapi.security.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            SecurityConfig.class,
            ApplicationConfig.class,
            JwtAuthenticationFilter.class
        }
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserConverter userConverter;

    @Test
    public void register_Success() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("test_user");
        request.setPassword("password");
        request.setEmail("test@test.com");

        User user = new User();
        user.setUserId(1);
        user.setUsername("test_user");

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(1);
        userDTO.setUsername("test_user");

        when(userConverter.registerToEntity(any(RegisterRequestDTO.class))).thenReturn(user);
        when(authService.register(any(User.class))).thenReturn(user);
        when(userConverter.toDto(user)).thenReturn(userDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test_user"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(authService, times(1)).register(any(User.class));
    }

    @Test
    public void login_Success() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("test_user");
        request.setPassword("password");

        User user = new User();
        user.setUsername("test_user");

        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .accessToken("access_token")
                .expiresIn(180L)
                .refreshToken("refresh_token")
                .build();

        when(userConverter.loginToEntity(any(LoginRequestDTO.class))).thenReturn(user);
        when(authService.login(any(User.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"));

        verify(authService, times(1)).login(any(User.class));
    }

    @Test
    public void me_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test_user");

        when(userConverter.toDto(any(User.class))).thenReturn(userDTO);

        mockMvc.perform(get("/auth/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"));
    }

    @Test
    public void me_Unauthenticated_ReturnsNull() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void refresh_Success() throws Exception {
        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setRefreshToken("refresh_token");

        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .accessToken("new_access_token")
                .expiresIn(180L)
                .refreshToken("refresh_token")
                .build();

        when(authService.refreshToken("refresh_token")).thenReturn(responseDTO);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_access_token"));

        verify(authService, times(1)).refreshToken("refresh_token");
    }
}
