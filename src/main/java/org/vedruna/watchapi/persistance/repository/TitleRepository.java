package org.vedruna.watchapi.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vedruna.watchapi.persistance.model.Title;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Title.
 */
@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {

    /**
     * Busca un título a partir de su ID de Watchmode.
     * 
     * @param watchmodeId Identificador externo de Watchmode.
     * @return Un Optional con el Title si se encuentra.
     */
    Optional<Title> findByWatchmodeId(Integer watchmodeId);
}
