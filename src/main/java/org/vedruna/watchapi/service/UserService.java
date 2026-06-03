package org.vedruna.watchapi.service;

import org.springframework.stereotype.Service;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar las operaciones relacionadas con la información de usuario.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username Nombre de usuario a buscar.
     * @return Entidad User encontrada.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    public User getUserByUsername(String username) {
        log.debug("Buscando en repositorio al usuario: '{}'", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Búsqueda fallida: usuario '{}' no existe", username);
                    return new ResourceNotFoundException("Usuario no encontrado con el nombre de usuario: " + username);
                });
    }

    /**
     * Actualiza el nombre de usuario del usuario autenticado actual, validando que el nuevo
     * nombre no esté ya en uso por otra cuenta.
     * 
     * @param currentUser Usuario autenticado actual.
     * @param newUsername Nuevo nombre de usuario deseado.
     * @return Entidad User actualizada.
     * @throws BadRequestException si el nombre de usuario ya está en uso.
     */
    public User updateUsername(User currentUser, String newUsername) {
        log.info("Actualizando nombre de usuario para ID {}. Nuevo nombre propuesto: '{}'", currentUser.getUserId(), newUsername);
        // Si el nombre es idéntico al actual, no hace falta realizar cambios
        if (currentUser.getUsername().equals(newUsername)) {
            log.info("El nuevo nombre coincide con el actual. No se realizan cambios.");
            return currentUser;
        }

        // Validar si el nombre de usuario ya está en uso por otro usuario
        userRepository.findByUsername(newUsername).ifPresent(existingUser -> {
            log.warn("El nombre de usuario '{}' ya está en uso por el usuario ID {}", newUsername, existingUser.getUserId());
            throw new BadRequestException("El nombre de usuario '" + newUsername + "' ya está en uso.");
        });

        // Buscar al usuario de forma persistente en la BBDD
        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + currentUser.getUserId()));

        // Actualizar y guardar
        user.setUsername(newUsername);
        User savedUser = userRepository.save(user);
        log.info("Nombre de usuario actualizado con éxito para el usuario ID {}", savedUser.getUserId());
        return savedUser;
    }
}
