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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * Controlador REST para gestionar la creación, modificación, eliminación y consulta de reseñas.
 */
@CrossOrigin
@RestController
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewConverter reviewConverter;

    /**
     * Endpoint público para obtener todas las reseñas de un título en base a su ID de Watchmode.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @return Lista de reseñas mapeadas a ReviewDTO.
     */
    @GetMapping("/titles/{watchmodeId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Integer watchmodeId) {
        return ResponseEntity.ok(reviewConverter.toDtoList(reviewService.getReviewsByTitle(watchmodeId)));
    }

    /**
     * Endpoint privado para insertar una nueva reseña vinculada a un título.
     * 
     * @param request Datos de la reseña a insertar.
     * @param user Usuario autenticado (inyectado por Spring Security).
     * @return ResponseEntity con la reseña creada.
     */
    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewRequestDTO request, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewConverter.toDto(reviewService.createReview(request, user)));
    }

    /**
     * Endpoint privado para editar una reseña propia.
     * 
     * @param id ID de la reseña.
     * @param request Datos de edición de la reseña.
     * @param user Usuario autenticado.
     * @return La reseña editada.
     */
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Integer id, @Valid @RequestBody ReviewEditRequestDTO request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewConverter.toDto(reviewService.updateReview(id, request, user)));
    }

    /**
     * Endpoint privado para borrar una reseña propia.
     * 
     * @param id ID de la reseña.
     * @param user Usuario autenticado.
     * @return Mensaje de éxito.
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id, user);
        return ResponseEntity.ok("Reseña eliminada exitosamente");
    }
}
