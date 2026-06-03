package org.vedruna.watchapi.security.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de renovación de tokens usando un Refresh Token.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDTO {

    @NotBlank(message = "El Refresh Token es obligatorio")
    private String refreshToken;
}
