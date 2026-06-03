package org.vedruna.watchapi.controller.converter;

import org.springframework.stereotype.Component;
import org.vedruna.watchapi.controller.dto.UserProfileDTO;
import org.vedruna.watchapi.persistance.model.User;

import lombok.AllArgsConstructor;

/**
 * Converter encargado de transformar la entidad User a UserProfileDTO 
 * para la visualización del perfil público.
 */
@Component
@AllArgsConstructor
public class UserProfileConverter {

    private final ReviewConverter reviewConverter;
    private final TitleConverter titleConverter;

    /**
     * Convierte una entidad User a su perfil público UserProfileDTO.
     * 
     * @param user Entidad User.
     * @return UserProfileDTO correspondiente.
     */
    public UserProfileDTO toProfileDto(User user) {
        if (user == null) {
            return null;
        }
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDescription(user.getDescription());
        dto.setCreateDate(user.getCreateDate());
        
        dto.setReviews(reviewConverter.toDtoList(user.getReviews()));
        dto.setFavoriteTitles(titleConverter.toDtoList(user.getFavoriteTitles()));
        
        return dto;
    }
}
