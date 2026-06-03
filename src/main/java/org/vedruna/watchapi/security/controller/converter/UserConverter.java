package org.vedruna.watchapi.security.controller.converter;

import org.springframework.stereotype.Component;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.controller.dto.LoginRequestDTO;
import org.vedruna.watchapi.security.controller.dto.RegisterRequestDTO;
import org.vedruna.watchapi.security.controller.dto.UserDTO;

/**
 * Componente encargado de la conversión de datos entre la entidad User y los DTOs de seguridad.
 */
@Component
public class UserConverter {

    /**
     * Convierte una entidad User a su DTO de salida UserDTO.
     * 
     * @param user Entidad de usuario.
     * @return UserDTO para enviar de forma segura al cliente.
     */
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDescription(user.getDescription());
        dto.setCreateDate(user.getCreateDate());
        return dto; 
    }

    /**
     * Convierte un DTO de login a una entidad User temporal.
     * 
     * @param request DTO de solicitud de inicio de sesión.
     * @return Entidad User con nombre de usuario y contraseña.
     */
    public User loginToEntity(LoginRequestDTO request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }

    /**
     * Convierte un DTO de registro a una entidad User.
     * 
     * @param request DTO de solicitud de registro.
     * @return Entidad User con los campos listos para el registro.
     */
    public User registerToEntity(RegisterRequestDTO request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setUsername(request.getUsername()); 
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setDescription(request.getDescription());
        return user;
    }
}
