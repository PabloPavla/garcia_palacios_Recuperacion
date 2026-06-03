package org.vedruna.watchapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.watchapi.controller.converter.TitleConverter;
import org.vedruna.watchapi.controller.dto.TitleDTO;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.service.TitleService;

import lombok.AllArgsConstructor;
import java.util.List;

/**
 * Controlador REST para gestionar la lista de películas y series favoritas de cada usuario.
 * Todos los endpoints bajo /favorites son privados (requieren autenticación).
 */
@CrossOrigin
@RestController
@RequestMapping("/favorites")
@AllArgsConstructor
public class FavoritesController {

    private final TitleService titleService;
    private final TitleConverter titleConverter;

    /**
     * Añade un título a la lista de favoritos del usuario autenticado.
     * Si no existe localmente, descarga los detalles desde Watchmode y los guarda en base de datos.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @param user Usuario autenticado (inyectado por Spring Security).
     * @return El TitleDTO del recurso añadido.
     */
    @PostMapping("/{watchmodeId}")
    public ResponseEntity<TitleDTO> addFavorite(@PathVariable Integer watchmodeId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(titleConverter.toDto(titleService.addFavorite(watchmodeId, user)));
    }

    /**
     * Elimina un título de la lista de favoritos del usuario autenticado.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @param user Usuario autenticado.
     * @return Mensaje de éxito.
     */
    @DeleteMapping("/{watchmodeId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable Integer watchmodeId, @AuthenticationPrincipal User user) {
        titleService.deleteFavorite(watchmodeId, user);
        return ResponseEntity.ok("Título eliminado de favoritos exitosamente");
    }

    /**
     * Obtiene todos los títulos favoritos del usuario logueado.
     * 
     * @param user Usuario autenticado.
     * @return Lista de TitleDTO de favoritos.
     */
    @GetMapping("/me")
    public ResponseEntity<List<TitleDTO>> getMyFavorites(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(titleConverter.toDtoList(titleService.getFavoriteTitles(user)));
    }
}
