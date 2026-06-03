package org.vedruna.watchapi.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.vedruna.watchapi.persistance.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class JWTServiceImplTest {

    private JWTServiceImpl jwtService;
    private User user;

    @BeforeEach
    public void setUp() {
        jwtService = new JWTServiceImpl();
        // Set private fields via ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "accessTokenSecretKey", "lMCvj7Sirkk41OpuXDBKoSA1YeQ4aTeHmP4gzoyoaLk=");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 180000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenSecretKey", "laCvj7Sirkk41OpuXDBKoSA1YeQ4aTeHmP4gzoyoaLk=");
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 604800000L);

        user = new User();
        user.setUserId(1);
        user.setUsername("test_user");
        user.setEmail("test@email.com");
    }

    @Test
    public void generateAccessToken_Success() {
        String token = jwtService.generateAccessToken(user);
        assertThat(token).isNotEmpty();

        String username = jwtService.getUsernameFromAccessToken(token);
        assertThat(username).isEqualTo("test_user");

        String email = jwtService.getClaim(token, claims -> claims.get("email", String.class), "lMCvj7Sirkk41OpuXDBKoSA1YeQ4aTeHmP4gzoyoaLk=");
        assertThat(email).isEqualTo("test@email.com");

        boolean isValid = jwtService.isAccessTokenValid(token, user);
        assertThat(isValid).isTrue();
    }

    @Test
    public void generateRefreshToken_Success() {
        String token = jwtService.generateRefreshToken(user);
        assertThat(token).isNotEmpty();

        String username = jwtService.getUsernameFromRefreshToken(token);
        assertThat(username).isEqualTo("test_user");

        boolean isValid = jwtService.isRefreshTokenValid(token, user);
        assertThat(isValid).isTrue();
    }

    @Test
    public void isAccessTokenValid_InvalidUser_ReturnsFalse() {
        String token = jwtService.generateAccessToken(user);

        User otherUser = new User();
        otherUser.setUsername("other_user");

        boolean isValid = jwtService.isAccessTokenValid(token, otherUser);
        assertThat(isValid).isFalse();
    }
}
