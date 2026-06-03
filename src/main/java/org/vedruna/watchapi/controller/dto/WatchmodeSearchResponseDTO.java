package org.vedruna.watchapi.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * DTO envoltorio para la lista de resultados devueltos por el endpoint de búsqueda de Watchmode.
 */
@Data
public class WatchmodeSearchResponseDTO {

    @JsonProperty("title_results")
    private List<WatchmodeSearchResultDTO> results;
}
