package org.vedruna.watchapi.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para la solicitud de creación de una nueva reseña.
 */
@Data
public class ReviewRequestDTO {

    @NotBlank(message = "El contenido de la reseña no puede estar vacío")
    private String content;

    @NotNull(message = "La calificación numérica es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 10, message = "La calificación máxima es 10")
    private Integer rating;

    @NotNull(message = "El ID de Watchmode del título es obligatorio")
    private Integer watchmodeId;
}
