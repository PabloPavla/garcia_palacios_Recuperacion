package org.vedruna.watchapi.security.controller.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * DTO que representa la información de un usuario sin exponer la contraseña.
 */
@Data
public class UserDTO {
    private Integer userId;
    private String username;
    private String email;
    private String description;
    private LocalDate createDate;
}
