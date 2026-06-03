package org.vedruna.watchapi.persistance.model;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * Entidad que representa un rol de usuario en la base de datos (por ejemplo, USER o ADMIN).
 */
@Data
@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "rol_name", unique = true, nullable = false, length = 5)
    private String rolName;

    @OneToMany(mappedBy = "userRol", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> users;
}
