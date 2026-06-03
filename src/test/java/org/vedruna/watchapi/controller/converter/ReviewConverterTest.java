package org.vedruna.watchapi.controller.converter;

import org.junit.jupiter.api.Test;
import org.vedruna.watchapi.controller.dto.ReviewDTO;
import org.vedruna.watchapi.persistance.model.Review;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewConverterTest {

    private final ReviewConverter reviewConverter = new ReviewConverter();

    @Test
    public void toDto_Success() {
        User user = new User();
        user.setUsername("test_user");

        Title title = new Title();
        title.setWatchmodeId(12345);

        Review review = new Review();
        review.setReviewId(10);
        review.setContent("Awesome!");
        review.setRating(9);
        review.setCreateDate(LocalDateTime.of(2026, 6, 3, 12, 0));
        review.setEditDate(LocalDateTime.of(2026, 6, 3, 13, 0));
        review.setUser(user);
        review.setTitle(title);

        ReviewDTO dto = reviewConverter.toDto(review);

        assertThat(dto).isNotNull();
        assertThat(dto.getReviewId()).isEqualTo(10);
        assertThat(dto.getContent()).isEqualTo("Awesome!");
        assertThat(dto.getRating()).isEqualTo(9);
        assertThat(dto.getCreateDate()).isEqualTo(LocalDateTime.of(2026, 6, 3, 12, 0));
        assertThat(dto.getEditDate()).isEqualTo(LocalDateTime.of(2026, 6, 3, 13, 0));
        assertThat(dto.getUsername()).isEqualTo("test_user");
        assertThat(dto.getWatchmodeId()).isEqualTo(12345);
    }

    @Test
    public void toDto_NullRelations_ReturnsDtoWithNulls() {
        Review review = new Review();
        review.setReviewId(10);
        review.setUser(null);
        review.setTitle(null);

        ReviewDTO dto = reviewConverter.toDto(review);

        assertThat(dto).isNotNull();
        assertThat(dto.getReviewId()).isEqualTo(10);
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getWatchmodeId()).isNull();
    }

    @Test
    public void toDto_NullInput_ReturnsNull() {
        assertThat(reviewConverter.toDto(null)).isNull();
    }

    @Test
    public void toDtoList_Success() {
        Review review1 = new Review();
        review1.setReviewId(1);
        Review review2 = new Review();
        review2.setReviewId(2);

        List<ReviewDTO> dtos = reviewConverter.toDtoList(List.of(review1, review2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getReviewId()).isEqualTo(1);
        assertThat(dtos.get(1).getReviewId()).isEqualTo(2);
    }

    @Test
    public void toDtoList_NullInput_ReturnsEmptyList() {
        assertThat(reviewConverter.toDtoList(null)).isEmpty();
    }
}
