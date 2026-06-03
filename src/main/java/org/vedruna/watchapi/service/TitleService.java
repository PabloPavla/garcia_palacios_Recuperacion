package org.vedruna.watchapi.service;

import org.springframework.stereotype.Service;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import lombok.AllArgsConstructor;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con las películas y series (titles).
 */
@Service
@AllArgsConstructor
public class TitleService {

    private final WatchmodeService watchmodeService;

    /**
     * Busca títulos llamando al servicio externo de Watchmode.
     * 
     * @param name Nombre del título.
     * @return DTO de respuesta con los resultados de la búsqueda.
     */
    public WatchmodeSearchResponseDTO searchTitles(String name) {
        return watchmodeService.searchTitlesByName(name);
    }
}
