package org.vedruna.watchapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.vedruna.watchapi.controller.converter.ReviewConverter;
import org.vedruna.watchapi.controller.dto.ReviewDTO;
import org.vedruna.watchapi.controller.dto.ReviewRequestDTO;
import org.vedruna.watchapi.controller.dto.ReviewEditRequestDTO;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * REST controller for managing reviews and ratings of movies and TV shows.
 */
@CrossOrigin
@RestController
@AllArgsConstructor
@Slf4j
@Tag(name = "Reseñas", description = "Endpoints para la creación, modificación, eliminación y consulta de reseñas")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewConverter reviewConverter;

    /**
     * Endpoint público para obtener todas las reseñas de un título en base a su ID de Watchmode.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @return Lista de reseñas mapeadas a ReviewDTO.
     */
    @Operation(summary = "Obtener reseñas por título", description = "Obtiene la lista completa de reseñas realizadas por cualquier usuario para un título en específico usando su watchmodeId. Endpoint público.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseñas obtenidas con éxito")
    })
    @GetMapping("/titles/{watchmodeId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Integer watchmodeId) {
        log.info("Solicitud pública para obtener reseñas del watchmodeId: {}", watchmodeId);
        List<ReviewDTO> reviews = reviewConverter.toDtoList(reviewService.getReviewsByTitle(watchmodeId));
        log.info("Se devolvieron {} reseñas para el watchmodeId: {}", reviews.size(), watchmodeId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Endpoint privado para insertar una nueva reseña vinculada a un título.
     * 
     * @param request Datos de la reseña a insertar.
     * @param user Usuario autenticado (inyectado por Spring Security).
     * @return ResponseEntity con la reseña creada.
     */
    @Operation(summary = "Crear reseña", description = "Crea una reseña con calificación numérica (1-10) vinculada a un título de Watchmode. Requiere autenticación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewRequestDTO request, @AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita crear reseña para watchmodeId: {}", user.getUsername(), request.getWatchmodeId());
        ReviewDTO review = reviewConverter.toDto(reviewService.createReview(request, user));
        log.info("Reseña creada con ID {} para watchmodeId {}", review.getReviewId(), request.getWatchmodeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    /**
     * Endpoint privado para editar una reseña propia.
     * 
     * @param id ID de la reseña.
     * @param request Datos de edición de la reseña.
     * @param user Usuario autenticado.
     * @return La reseña editada.
     */
    @Operation(summary = "Editar reseña", description = "Modifica una reseña existente. El usuario autenticado debe ser el autor de la reseña. Requiere autenticación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña editada con éxito"),
        @ApiResponse(responseCode = "400", description = "La reseña no pertenece al usuario autenticado"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Integer id, @Valid @RequestBody ReviewEditRequestDTO request, @AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita editar la reseña con ID: {}", user.getUsername(), id);
        ReviewDTO review = reviewConverter.toDto(reviewService.updateReview(id, request, user));
        log.info("Reseña con ID {} editada con éxito", id);
        return ResponseEntity.ok(review);
    }

    /**
     * Endpoint privado para borrar una reseña propia.
     * 
     * @param id ID de la reseña.
     * @param user Usuario autenticado.
     * @return Mensaje de éxito.
     */
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña del sistema. El usuario autenticado debe ser el autor de la reseña. Requiere autenticación.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña eliminada con éxito"),
        @ApiResponse(responseCode = "400", description = "La reseña no pertenece al usuario autenticado"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        log.info("Usuario '{}' solicita borrar la reseña con ID: {}", user.getUsername(), id);
        reviewService.deleteReview(id, user);
        log.info("Reseña con ID {} eliminada con éxito", id);
        return ResponseEntity.ok("Reseña eliminada exitosamente");
    }
}
