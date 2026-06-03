package org.vedruna.watchapi.service;

import org.springframework.stereotype.Service;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.controller.dto.WatchmodeTitleDetailsDTO;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.TitleRepository;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con las películas y series (titles)
 * y la gestión de favoritos del usuario.
 */
@Service
@AllArgsConstructor
public class TitleService {

    private final WatchmodeService watchmodeService;
    private final TitleRepository titleRepository;
    private final UserRepository userRepository;

    /**
     * Busca títulos llamando al servicio externo de Watchmode.
     * 
     * @param name Nombre del título.
     * @return DTO de respuesta con los resultados de la búsqueda.
     */
    public WatchmodeSearchResponseDTO searchTitles(String name) {
        return watchmodeService.searchTitlesByName(name);
    }

    /**
     * Añade un título a la lista de favoritos del usuario autenticado.
     * Si el título no existe localmente en la base de datos, lo consulta en la API externa de Watchmode,
     * lo guarda en local, y luego lo añade a favoritos.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @param user Usuario actual autenticado.
     * @return La entidad Title añadida a favoritos.
     */
    public Title addFavorite(Integer watchmodeId, User user) {
        // 1. Busca el título en la base de datos local, o lo descarga y guarda de la API externa
        Title title = titleRepository.findByWatchmodeId(watchmodeId)
                .orElseGet(() -> {
                    WatchmodeTitleDetailsDTO details = watchmodeService.getTitleDetails(watchmodeId);
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
                    
                    return titleRepository.save(newTitle);
                });

        // 2. Carga la entidad User fresca desde la base de datos para evitar desasociación JPA
        User userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID " + user.getUserId()));

        if (userEntity.getFavoriteTitles() == null) {
            userEntity.setFavoriteTitles(new ArrayList<>());
        }

        // 3. Si el título no está ya en favoritos, lo añade y guarda
        if (!userEntity.getFavoriteTitles().contains(title)) {
            userEntity.getFavoriteTitles().add(title);
            userRepository.save(userEntity);
        }

        return title;
    }

    /**
     * Elimina un título de la lista de favoritos del usuario autenticado.
     * 
     * @param watchmodeId ID de Watchmode del título.
     * @param user Usuario actual autenticado.
     */
    public void deleteFavorite(Integer watchmodeId, User user) {
        Title title = titleRepository.findByWatchmodeId(watchmodeId)
                .orElseThrow(() -> new ResourceNotFoundException("El título con Watchmode ID " + watchmodeId + " no existe en favoritos."));

        User userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID " + user.getUserId()));

        if (userEntity.getFavoriteTitles() == null || !userEntity.getFavoriteTitles().contains(title)) {
            throw new ResourceNotFoundException("El título no se encuentra en tu lista de favoritos");
        }

        userEntity.getFavoriteTitles().remove(title);
        userRepository.save(userEntity);
    }

    /**
     * Obtiene todos los títulos favoritos del usuario autenticado.
     * 
     * @param user Usuario actual.
     * @return Lista de títulos favoritos.
     */
    public List<Title> getFavoriteTitles(User user) {
        User userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID " + user.getUserId()));

        return userEntity.getFavoriteTitles() != null ? userEntity.getFavoriteTitles() : List.of();
    }
}
