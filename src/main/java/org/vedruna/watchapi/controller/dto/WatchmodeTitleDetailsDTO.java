package org.vedruna.watchapi.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * DTO que representa los detalles completos de un título devuelto por la API de Watchmode.
 */
@Data
public class WatchmodeTitleDetailsDTO {

    private Integer id;
    
    private String title;
    
    private String type;
    
    private Integer year;
    
    @JsonProperty("genre_names")
    private List<String> genreNames;
}
