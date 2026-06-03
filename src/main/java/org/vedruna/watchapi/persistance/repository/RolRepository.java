package org.vedruna.watchapi.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vedruna.watchapi.persistance.model.Rol;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Rol.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * Busca un rol a partir de su nombre.
     * 
     * @param rolName Nombre del rol.
     * @return Un Optional con el Rol si se encuentra.
     */
    Optional<Rol> findByRolName(String rolName);
}
