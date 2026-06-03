package org.vedruna.watchapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.service.TitleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar la búsqueda y detalles de títulos (películas y series).
 */
@CrossOrigin
@RestController
@RequestMapping("/titles")
@AllArgsConstructor
@Slf4j
@Tag(name = "Títulos", description = "Endpoints para la búsqueda y consulta de películas y series")
public class TitleController {

    private final TitleService titleService;

    /**
     * Endpoint privado para buscar títulos en la API de Watchmode por su nombre.
     * 
     * @param name Nombre del título.
     * @return ResponseEntity con la lista de resultados.
     */
    @Operation(summary = "Buscar títulos", description = "Busca títulos (películas y series) en la API externa de Watchmode por su nombre. Requiere autenticación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada con éxito"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (falta o expira el token JWT)")
    })
    @GetMapping("/search")
    public ResponseEntity<WatchmodeSearchResponseDTO> search(@RequestParam String name) {
        log.info("Recibida solicitud de búsqueda de títulos con nombre: {}", name);
        WatchmodeSearchResponseDTO response = titleService.searchTitles(name);
        log.info("Búsqueda finalizada con éxito para: {}", name);
        return ResponseEntity.ok(response);
    }
}
