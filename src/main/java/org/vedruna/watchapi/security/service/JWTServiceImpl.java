package org.vedruna.watchapi.security.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.vedruna.watchapi.persistance.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

/**
 * Servicio de implementación para la creación, firma, y validación de tokens JWT.
 * Esta clase utiliza la librería io.jsonwebtoken (JJWT) para manejar tanto los 
 * Access Tokens (corta duración) como los Refresh Tokens (larga duración).
 */
@Service
public class JWTServiceImpl {

    /**
     * Clave secreta (Base64) utilizada para firmar y verificar el Access Token.
     */
    @Value("${auth.access-token-secret-key}")
    private String accessTokenSecretKey;

    /**
     * Tiempo de vida del Access Token en milisegundos.
     */
    @Value("${auth.access-token-expiration}")
    private Long accessTokenExpiration;

    /**
     * Clave secreta (Base64) utilizada para firmar y verificar el Refresh Token.
     */
    @Value("${auth.refresh-token-secret-key}")
    private String refreshTokenSecretKey;

    /**
     * Tiempo de vida del Refresh Token en milisegundos.
     */
    @Value("${auth.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Convierte la clave secreta Base64 inyectada en un objeto SecretKey compatible con JJWT.
     *
     * @param secretKey La clave secreta en formato Base64.
     * @return La clave de firma como SecretKey.
     */
    public SecretKey getKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Método centralizado para la construcción de cualquier tipo de token JWT.
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails user, Long expirationTime, String secretKey) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey(secretKey))
                .compact(); 
    }

    /**
     * Genera el Access Token, el cual se utiliza para acceder a los recursos protegidos.
     *
     * @param user La entidad de usuario que será el sujeto del token.
     * @return El Access Token JWT.
     */
    public String generateAccessToken(User user) {
        return buildToken(Map.of(
            "email", user.getEmail()
        ), user, accessTokenExpiration, accessTokenSecretKey);
    }

    /**
     * Genera el Refresh Token, el cual se utiliza únicamente para solicitar un nuevo Access Token.
     *
     * @param user La entidad de usuario que será el sujeto del token.
     * @return El Refresh Token JWT.
     */
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshTokenExpiration, refreshTokenSecretKey);
    }

    /**
     * Obtiene el payload completo (Claims) de un token después de verificar su firma y expiración.
     */
    private Claims getAllClaims(String token, String secretKey) {
        return Jwts
                .parser()
                .verifyWith(getKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Método genérico para extraer un claim específico usando una función resolutora.
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = getAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene el nombre de usuario (Subject) del Access Token.
     */
    public String getUsernameFromAccessToken(String token) {
        return getClaim(token, Claims::getSubject, accessTokenSecretKey);
    }

    /**
     * Obtiene el tiempo de expiración (en segundos) configurado para el Access Token.
     */
    public Long getAccessTokenExpiresIn() {
        return accessTokenExpiration/1000;
    }

    /**
     * Obtiene la fecha de expiración ('exp') del Access Token.
     */
    private Date getAccessTokenExpiration(String token) {
        return getClaim(token, Claims::getExpiration, accessTokenSecretKey);
    }

    /**
     * Verifica si el Access Token ha caducado.
     */
    private boolean isAccessTokenExpired(String token) {
        return getAccessTokenExpiration(token).before(new Date());
    }

    /**
     * Realiza la validación lógica final del Access Token.
     */
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromAccessToken(token);
        return (username.equals(userDetails.getUsername()) && !isAccessTokenExpired(token));
    }

    /**
     * Obtiene el nombre de usuario (Subject) del Refresh Token.
     */
    public String getUsernameFromRefreshToken(String token) {
        return getClaim(token, Claims::getSubject, refreshTokenSecretKey);
    }

    /**
     * Obtiene el tiempo de expiración (en segundos) configurado para el Refresh Token.
     */
    public Long getRefreshTokenExpiresIn() {
        return refreshTokenExpiration/1000;
    }

    /**
     * Obtiene la fecha de expiración ('exp') del Refresh Token.
     */
    private Date getRefreshTokenExpiration(String token) {
        return getClaim(token, Claims::getExpiration, refreshTokenSecretKey);
    }

    /**
     * Verifica si el Refresh Token ha caducado.
     */
    private boolean isRefreshTokenExpired(String token) {
        return getRefreshTokenExpiration(token).before(new Date());
    }

    /**
     * Realiza la validación lógica final del Refresh Token.
     */
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromRefreshToken(token);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token));
    }
}
