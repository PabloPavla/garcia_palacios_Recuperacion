package org.vedruna.watchapi.controller.converter;

import org.springframework.stereotype.Component;
import org.vedruna.watchapi.controller.dto.ReviewDTO;
import org.vedruna.watchapi.persistance.model.Review;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter encargado de transformar las entidades Review a sus correspondientes DTOs de salida.
 */
@Component
public class ReviewConverter {

    /**
     * Convierte una entidad Review a ReviewDTO.
     * 
     * @param review Entidad Review.
     * @return ReviewDTO correspondiente.
     */
    public ReviewDTO toDto(Review review) {
        if (review == null) {
            return null;
        }
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setRating(review.getRating());
        dto.setCreateDate(review.getCreateDate());
        dto.setEditDate(review.getEditDate());
        
        if (review.getUser() != null) {
            dto.setUsername(review.getUser().getUsername());
        }
        if (review.getTitle() != null) {
            dto.setWatchmodeId(review.getTitle().getWatchmodeId());
        }
        
        return dto;
    }

    /**
     * Convierte una lista de entidades Review a una lista de DTOs.
     * 
     * @param reviews Lista de entidades.
     * @return Lista de DTOs.
     */
    public List<ReviewDTO> toDtoList(List<Review> reviews) {
        if (reviews == null) {
            return List.of();
        }
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
