package org.vedruna.watchapi.persistance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reseña realizada por un usuario sobre un título.
 */
@Data
@Entity
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "edit_date")
    private LocalDateTime editDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titles_title_id", nullable = false)
    private Title title;
}
