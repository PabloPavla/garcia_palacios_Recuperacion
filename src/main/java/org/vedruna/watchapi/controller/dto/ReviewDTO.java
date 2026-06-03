package org.vedruna.watchapi.controller.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO que representa los detalles de salida de una reseña.
 */
@Data
public class ReviewDTO {
    private Integer reviewId;
    private String content;
    private Integer rating;
    private LocalDateTime createDate;
    private LocalDateTime editDate;
    private String username;
    private Integer watchmodeId;
}
