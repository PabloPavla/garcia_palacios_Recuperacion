package org.vedruna.watchapi.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vedruna.watchapi.persistance.model.Review;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para la entidad Review.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Busca reseñas asociadas a un título por su ID de Watchmode.
     * 
     * @param watchmodeId Identificador externo de Watchmode.
     * @return Lista de reseñas encontradas.
     */
    List<Review> findByTitleWatchmodeId(Integer watchmodeId);

    /**
     * Busca reseñas escritas por un usuario por su nombre de usuario.
     * 
     * @param username Nombre del usuario autor.
     * @return Lista de reseñas encontradas.
     */
    List<Review> findByUserUsername(String username);
}
