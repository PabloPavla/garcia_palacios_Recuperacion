package org.vedruna.watchapi.security.controller.converter;

import org.junit.jupiter.api.Test;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.controller.dto.LoginRequestDTO;
import org.vedruna.watchapi.security.controller.dto.RegisterRequestDTO;
import org.vedruna.watchapi.security.controller.dto.UserDTO;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserConverterTest {

    private final UserConverter userConverter = new UserConverter();

    @Test
    public void toDto_Success() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("test_user");
        user.setEmail("test@email.com");
        user.setDescription("Description");
        user.setCreateDate(LocalDate.of(2026, 6, 3));

        UserDTO dto = userConverter.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(1);
        assertThat(dto.getUsername()).isEqualTo("test_user");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
        assertThat(dto.getDescription()).isEqualTo("Description");
        assertThat(dto.getCreateDate()).isEqualTo(LocalDate.of(2026, 6, 3));
    }

    @Test
    public void toDto_NullInput_ReturnsNull() {
        assertThat(userConverter.toDto(null)).isNull();
    }

    @Test
    public void loginToEntity_Success() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("user");
        request.setPassword("pass");

        User user = userConverter.loginToEntity(request);

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("user");
        assertThat(user.getPassword()).isEqualTo("pass");
    }

    @Test
    public void loginToEntity_NullInput_ReturnsNull() {
        assertThat(userConverter.loginToEntity(null)).isNull();
    }

    @Test
    public void registerToEntity_Success() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("user");
        request.setPassword("pass");
        request.setEmail("email@test.com");
        request.setDescription("description");

        User user = userConverter.registerToEntity(request);

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("user");
        assertThat(user.getPassword()).isEqualTo("pass");
        assertThat(user.getEmail()).isEqualTo("email@test.com");
        assertThat(user.getDescription()).isEqualTo("description");
    }

    @Test
    public void registerToEntity_NullInput_ReturnsNull() {
        assertThat(userConverter.registerToEntity(null)).isNull();
    }
}
