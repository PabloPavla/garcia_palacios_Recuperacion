package org.vedruna.watchapi.service;

import org.springframework.stereotype.Service;
import org.vedruna.watchapi.controller.dto.ReviewRequestDTO;
import org.vedruna.watchapi.controller.dto.ReviewEditRequestDTO;
import org.vedruna.watchapi.controller.dto.WatchmodeTitleDetailsDTO;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.Review;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.ReviewRepository;
import org.vedruna.watchapi.persistance.repository.TitleRepository;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar las operaciones y lógica de negocio relacionadas con las reseñas.
 */
@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TitleRepository titleRepository;
    private final UserRepository userRepository;
    private final WatchmodeService watchmodeService;

    /**
     * Obtiene todas las reseñas asociadas a un título en base a su ID de Watchmode.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @return Lista de reseñas encontradas.
     */
    public List<Review> getReviewsByTitle(Integer watchmodeId) {
        log.info("Buscando en repositorio reseñas para el watchmodeId: {}", watchmodeId);
        List<Review> reviews = reviewRepository.findByTitleWatchmodeId(watchmodeId);
        log.info("Se encontraron {} reseñas para el watchmodeId: {}", reviews.size(), watchmodeId);
        return reviews;
    }

    /**
     * Crea una nueva reseña asociada a un título. Si el título no existe localmente,
     * descarga sus detalles desde la API de Watchmode y lo registra en base de datos.
     * 
     * @param request Datos de la reseña.
     * @param user Usuario autenticado que escribe la reseña.
     * @return La entidad Review guardada.
     */
    public Review createReview(ReviewRequestDTO request, User user) {
        log.info("Creando nueva reseña para watchmodeId: {} por usuario: {}", request.getWatchmodeId(), user.getUsername());
        // 1. Obtiene el título localmente o lo descarga desde la API externa de Watchmode
        Title title = titleRepository.findByWatchmodeId(request.getWatchmodeId())
                .orElseGet(() -> {
                    log.info("Título con watchmodeId {} no encontrado localmente. Descargando de Watchmode API...", request.getWatchmodeId());
                    WatchmodeTitleDetailsDTO details = watchmodeService.getTitleDetails(request.getWatchmodeId());
                    Title newTitle = new Title();
                    newTitle.setWatchmodeId(details.getId());
                    newTitle.setTitleName(details.getTitle());
                    newTitle.setType(details.getType());
                    newTitle.setYear(details.getYear());
                    
                    if (details.getGenreNames() != null && !details.getGenreNames().isEmpty()) {
                        newTitle.setGenre(String.join(", ", details.getGenreNames()));
                    } else {
                        newTitle.setGenre("Unknown");
                    }
                    
                    Title saved = titleRepository.save(newTitle);
                    log.info("Título '{}' guardado localmente con éxito", saved.getTitleName());
                    return saved;
                });

        // 2. Obtiene la entidad del usuario logueado desde la base de datos
        User userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID " + user.getUserId()));

        // 3. Crea la reseña
        Review review = new Review();
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setCreateDate(LocalDateTime.now());
        review.setUser(userEntity);
        review.setTitle(title);

        Review savedReview = reviewRepository.save(review);
        log.info("Reseña creada con éxito. ID: {}, Rating: {}", savedReview.getReviewId(), savedReview.getRating());
        return savedReview;
    }

    /**
     * Modifica el contenido y calificación de una reseña propia del usuario.
     * 
     * @param reviewId ID de la reseña a modificar.
     * @param request Nuevos datos de la reseña.
     * @param user Usuario autenticado que solicita la edición.
     * @return La entidad Review modificada.
     */
    public Review updateReview(Integer reviewId, ReviewEditRequestDTO request, User user) {
        log.info("Actualizando reseña ID: {} por usuario: {}", reviewId, user.getUsername());
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID " + reviewId));

        // Validación de propiedad
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            log.warn("El usuario '{}' intentó editar la reseña ID {} que pertenece al usuario ID {}", 
                     user.getUsername(), reviewId, review.getUser().getUserId());
            throw new BadRequestException("No tienes permisos para editar esta reseña porque no te pertenece.");
        }

        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setEditDate(LocalDateTime.now());

        Review updatedReview = reviewRepository.save(review);
        log.info("Reseña ID {} actualizada con éxito", updatedReview.getReviewId());
        return updatedReview;
    }

    /**
     * Borra una reseña propia del usuario logueado.
     * 
     * @param reviewId ID de la reseña a borrar.
     * @param user Usuario autenticado.
     */
    public void deleteReview(Integer reviewId, User user) {
        log.info("Eliminando reseña ID: {} por usuario: {}", reviewId, user.getUsername());
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID " + reviewId));

        // Validación de propiedad
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            log.warn("El usuario '{}' intentó eliminar la reseña ID {} que pertenece al usuario ID {}", 
                     user.getUsername(), reviewId, review.getUser().getUserId());
            throw new BadRequestException("No tienes permisos para eliminar esta reseña porque no te pertenece.");
        }

        reviewRepository.delete(review);
        log.info("Reseña ID {} eliminada con éxito del repositorio", reviewId);
    }
}
