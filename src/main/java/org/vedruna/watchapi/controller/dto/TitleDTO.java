package org.vedruna.watchapi.controller.dto;

import lombok.Data;

/**
 * DTO que representa un título para las respuestas de la API.
 */
@Data
public class TitleDTO {
    private Integer titleId;
    private Integer watchmodeId;
    private String titleName;
    private String type;
    private Integer year;
    private String genre;
}
