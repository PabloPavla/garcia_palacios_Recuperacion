package org.vedruna.watchapi.controller;

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
import org.vedruna.watchapi.controller.converter.UserProfileConverter;
import org.vedruna.watchapi.controller.dto.UserProfileDTO;
import org.vedruna.watchapi.controller.dto.UsernameUpdateRequestDTO;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.config.ApplicationConfig;
import org.vedruna.watchapi.security.config.SecurityConfig;
import org.vedruna.watchapi.security.controller.converter.UserConverter;
import org.vedruna.watchapi.security.controller.dto.UserDTO;
import org.vedruna.watchapi.security.filter.JwtAuthenticationFilter;
import org.vedruna.watchapi.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = UserController.class,
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserProfileConverter userProfileConverter;

    @MockBean
    private UserConverter userConverter;

    @Test
    public void getUserProfile_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername("test_user");

        when(userService.getUserByUsername("test_user")).thenReturn(user);
        when(userProfileConverter.toProfileDto(user)).thenReturn(profileDTO);

        mockMvc.perform(get("/users/test_user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"));

        verify(userService, times(1)).getUserByUsername("test_user");
    }

    @Test
    public void getUserProfile_NotFound() throws Exception {
        when(userService.getUserByUsername("non_existent")).thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/users/non_existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUsername_Success() throws Exception {
        User user = new User();
        user.setUsername("old_username");

        UsernameUpdateRequestDTO request = new UsernameUpdateRequestDTO();
        request.setUsername("new_username");

        User updatedUser = new User();
        updatedUser.setUsername("new_username");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("new_username");

        when(userService.updateUsername(any(User.class), eq("new_username"))).thenReturn(updatedUser);
        when(userConverter.toDto(updatedUser)).thenReturn(userDTO);

        mockMvc.perform(patch("/users/me/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new_username"));
    }
}
