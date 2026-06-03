package org.vedruna.watchapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.service.TitleService;

import lombok.AllArgsConstructor;

/**
 * Controlador REST para gestionar la búsqueda y detalles de títulos (películas y series).
 */
@CrossOrigin
@RestController
@RequestMapping("/titles")
@AllArgsConstructor
public class TitleController {

    private final TitleService titleService;

    /**
     * Endpoint privado para buscar títulos en la API de Watchmode por su nombre.
     * 
     * @param name Nombre del título.
     * @return ResponseEntity con la lista de resultados.
     */
    @GetMapping("/search")
    public ResponseEntity<WatchmodeSearchResponseDTO> search(@RequestParam String name) {
        return ResponseEntity.ok(titleService.searchTitles(name));
    }
}
