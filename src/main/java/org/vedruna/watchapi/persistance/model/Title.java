package org.vedruna.watchapi.persistance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * Entidad que representa un título (película o serie) vinculado desde la API de Watchmode.
 */
@Data
@Entity
@Table(name = "titles")
@NoArgsConstructor
@AllArgsConstructor
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "title_id")
    private Integer titleId;

    @Column(name = "watchmode_id", unique = true, nullable = false)
    private Integer watchmodeId;

    @Column(name = "title_name", nullable = false)
    private String titleName;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "genre")
    private String genre;

    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews;

    @ManyToMany(mappedBy = "favoriteTitles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> favoritedBy;
}
