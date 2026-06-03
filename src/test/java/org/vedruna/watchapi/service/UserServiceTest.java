package org.vedruna.watchapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1);
        user.setUsername("username_test");
        user.setEmail("test@email.com");
    }

    @Test
    public void getUserByUsername_Success() {
        when(userRepository.findByUsername("username_test")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("username_test");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("username_test");
        verify(userRepository, times(1)).findByUsername("username_test");
    }

    @Test
    public void getUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findByUsername("not_found")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("not_found"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con el nombre de usuario: not_found");
    }

    @Test
    public void updateUsername_SameUsername_DoesNothing() {
        User result = userService.updateUsername(user, "username_test");

        assertThat(result).isEqualTo(user);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void updateUsername_UsernameInUse_ThrowsException() {
        User existingUser = new User();
        existingUser.setUserId(2);
        existingUser.setUsername("in_use");

        when(userRepository.findByUsername("in_use")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUsername(user, "in_use"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El nombre de usuario 'in_use' ya está en uso.");
        
        verify(userRepository, never()).save(any());
    }

    @Test
    public void updateUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("new_username")).thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUsername(user, "new_username"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 1");
    }

    @Test
    public void updateUsername_Success() {
        when(userRepository.findByUsername("new_username")).thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUsername(user, "new_username");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("new_username");
        verify(userRepository, times(1)).save(user);
    }
}
