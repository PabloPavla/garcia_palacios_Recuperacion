package org.vedruna.watchapi.persistance.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa un usuario y sus detalles de seguridad para la autenticación.
 */
@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", unique = true, nullable = false, length = 20)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 90)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "description")
    private String description;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles_rol_id", referencedColumnName = "rol_id", nullable = false)
    private Rol userRol;

    /**
     * Devuelve las autoridades concedidas al usuario mapeándolas con el prefijo 'ROLE_'.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.userRol == null || this.userRol.getRolName() == null) {
            return Collections.emptyList();
        }
        String authorityName = "ROLE_" + this.userRol.getRolName().toUpperCase();
        return List.of(new SimpleGrantedAuthority(authorityName));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
