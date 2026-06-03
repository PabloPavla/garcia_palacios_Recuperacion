package org.vedruna.watchapi.controller.converter;

import org.springframework.stereotype.Component;
import org.vedruna.watchapi.controller.dto.TitleDTO;
import org.vedruna.watchapi.persistance.model.Title;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter encargado de transformar las entidades Title a sus correspondientes DTOs.
 */
@Component
public class TitleConverter {

    /**
     * Convierte una entidad Title a TitleDTO.
     * 
     * @param title Entidad Title.
     * @return TitleDTO correspondiente.
     */
    public TitleDTO toDto(Title title) {
        if (title == null) {
            return null;
        }
        TitleDTO dto = new TitleDTO();
        dto.setTitleId(title.getTitleId());
        dto.setWatchmodeId(title.getWatchmodeId());
        dto.setTitleName(title.getTitleName());
        dto.setType(title.getType());
        dto.setYear(title.getYear());
        dto.setGenre(title.getGenre());
        return dto;
    }

    /**
     * Convierte una lista de entidades Title a una lista de DTOs.
     * 
     * @param titles Lista de entidades.
     * @return Lista de DTOs.
     */
    public List<TitleDTO> toDtoList(List<Title> titles) {
        if (titles == null) {
            return List.of();
        }
        return titles.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
