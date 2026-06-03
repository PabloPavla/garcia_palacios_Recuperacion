package org.vedruna.watchapi.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vedruna.watchapi.persistance.model.User;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username Nombre de usuario.
     * @return Un Optional con el User si se encuentra.
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * 
     * @param email Correo electrónico.
     * @return Un Optional con el User si se encuentra.
     */
    Optional<User> findByEmail(String email);
}
