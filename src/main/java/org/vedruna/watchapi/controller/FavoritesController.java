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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Controlador REST para gestionar la lista de películas y series favoritas de cada usuario.
 * Todos los endpoints bajo /favorites requieren autenticación.
 */
@CrossOrigin
@RestController
@RequestMapping("/favorites")
@AllArgsConstructor
@Slf4j
@Tag(name = "Favoritos", description = "Endpoints para la gestión de películas y series favoritas del usuario")
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
    @Operation(summary = "Añadir a favoritos", description = "Agrega una película o serie a la lista de favoritos del usuario autenticado actual. Si el título no existe en el sistema, lo descarga de Watchmode.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Añadido a favoritos correctamente"),
        @ApiResponse(responseCode = "400", description = "El título ya está en la lista de favoritos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/{watchmodeId}")
    public ResponseEntity<TitleDTO> addFavorite(@PathVariable Integer watchmodeId, @AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita añadir el watchmodeId {} a favoritos", user.getUsername(), watchmodeId);
        TitleDTO favorite = titleConverter.toDto(titleService.addFavorite(watchmodeId, user));
        log.info("WatchmodeId {} añadido con éxito a favoritos del usuario '{}'", watchmodeId, user.getUsername());
        return ResponseEntity.ok(favorite);
    }

    /**
     * Elimina un título de la lista de favoritos del usuario autenticado.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @param user Usuario autenticado.
     * @return Mensaje de éxito.
     */
    @Operation(summary = "Eliminar de favoritos", description = "Quita una película o serie de la lista de favoritos del usuario autenticado actual.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eliminado de favoritos correctamente"),
        @ApiResponse(responseCode = "400", description = "El título no estaba en la lista de favoritos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{watchmodeId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable Integer watchmodeId, @AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita eliminar el watchmodeId {} de favoritos", user.getUsername(), watchmodeId);
        titleService.deleteFavorite(watchmodeId, user);
        log.info("WatchmodeId {} eliminado con éxito de favoritos del usuario '{}'", watchmodeId, user.getUsername());
        return ResponseEntity.ok("Título eliminado de favoritos exitosamente");
    }

    /**
     * Obtiene todos los títulos favoritos del usuario logueado.
     * 
     * @param user Usuario autenticado.
     * @return Lista de TitleDTO de favoritos.
     */
    @Operation(summary = "Obtener mis favoritos", description = "Devuelve la lista completa de películas y series que el usuario autenticado tiene en favoritos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Favoritos obtenidos correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/me")
    public ResponseEntity<List<TitleDTO>> getMyFavorites(@AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita obtener todos sus favoritos", user.getUsername());
        List<TitleDTO> favorites = titleConverter.toDtoList(titleService.getFavoriteTitles(user));
        log.info("Devueltos {} favoritos para el usuario '{}'", favorites.size(), user.getUsername());
        return ResponseEntity.ok(favorites);
    }
}
