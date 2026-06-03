package org.vedruna.watchapi.controller.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * DTO que representa el perfil público de un usuario, incluyendo su información básica,
 * sus reseñas y sus títulos favoritos.
 */
@Data
public class UserProfileDTO {
    private Integer userId;
    private String username;
    private String email;
    private String description;
    private LocalDate createDate;
    private List<ReviewDTO> reviews;
    private List<TitleDTO> favoriteTitles;
}
