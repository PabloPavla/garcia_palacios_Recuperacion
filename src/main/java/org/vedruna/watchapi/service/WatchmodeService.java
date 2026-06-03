package org.vedruna.watchapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.controller.dto.WatchmodeTitleDetailsDTO;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio encargado de consumir la API externa de Watchmode.
 */
@Service
@Slf4j
public class WatchmodeService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    /**
     * Constructor con inyección de dependencias y propiedades.
     */
    public WatchmodeService(
            RestTemplate restTemplate, 
            @Value("${watchmode.api-key}") String apiKey, 
            @Value("${watchmode.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * Busca títulos (películas/series) en la API de Watchmode por su nombre.
     * 
     * @param name Nombre a buscar.
     * @return El objeto de respuesta de búsqueda de Watchmode.
     */
    public WatchmodeSearchResponseDTO searchTitlesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("El parámetro de búsqueda 'name' no puede estar vacío");
        }
        
        String url = baseUrl + "/search/?apiKey={apiKey}&search_field=name&search_value={name}";
        log.info("Buscando títulos por nombre '{}' en Watchmode API...", name);

        try {
            WatchmodeSearchResponseDTO response = restTemplate.getForObject(url, WatchmodeSearchResponseDTO.class, apiKey, name);
            if (response == null || response.getResults() == null) {
                return new WatchmodeSearchResponseDTO();
            }
            return response;
        } catch (Exception e) {
            log.error("Error al consumir la API de Watchmode al buscar '{}': {}", name, e.getMessage());
            throw new BadRequestException("Error al conectar con el servicio externo de Watchmode: " + e.getMessage());
        }
    }

    /**
     * Obtiene los detalles completos de un título por su identificador de Watchmode.
     * 
     * @param watchmodeId ID de Watchmode.
     * @return Detalles del título.
     */
    public WatchmodeTitleDetailsDTO getTitleDetails(Integer watchmodeId) {
        if (watchmodeId == null) {
            throw new BadRequestException("El ID de Watchmode es requerido");
        }

        String url = baseUrl + "/title/{watchmodeId}/details/?apiKey={apiKey}";
        log.info("Obteniendo detalles del título con watchmodeId {}...", watchmodeId);

        try {
            WatchmodeTitleDetailsDTO response = restTemplate.getForObject(url, WatchmodeTitleDetailsDTO.class, watchmodeId, apiKey);
            if (response == null || response.getId() == null) {
                throw new ResourceNotFoundException("No se encontró el título en la API externa de Watchmode con el ID " + watchmodeId);
            }
            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al consumir la API de Watchmode para detalles de ID {}: {}", watchmodeId, e.getMessage());
            throw new ResourceNotFoundException("No se pudo obtener información del título desde Watchmode: " + e.getMessage());
        }
    }
}
