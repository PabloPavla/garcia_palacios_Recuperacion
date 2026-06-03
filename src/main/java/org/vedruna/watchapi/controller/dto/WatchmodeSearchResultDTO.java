package org.vedruna.watchapi.controller.dto;

import lombok.Data;

/**
 * DTO que representa un título individual retornado en los resultados de búsqueda de Watchmode.
 */
@Data
public class WatchmodeSearchResultDTO {
    private Integer id;
    private String name;
    private String type;
    private Integer year;
}
