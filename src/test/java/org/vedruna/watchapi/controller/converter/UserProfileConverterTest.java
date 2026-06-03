package org.vedruna.watchapi.controller.converter;

import org.junit.jupiter.api.Test;
import org.vedruna.watchapi.controller.dto.UserProfileDTO;
import org.vedruna.watchapi.persistance.model.Review;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProfileConverterTest {

    private final ReviewConverter reviewConverter = new ReviewConverter();
    private final TitleConverter titleConverter = new TitleConverter();
    private final UserProfileConverter userProfileConverter = new UserProfileConverter(reviewConverter, titleConverter);

    @Test
    public void toProfileDto_Success() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("username_test");
        user.setEmail("test@email.com");
        user.setDescription("This is a bio");
        user.setCreateDate(LocalDate.of(2026, 6, 3));

        Title title = new Title();
        title.setWatchmodeId(101);
        title.setTitleName("Favorite Movie");
        user.setFavoriteTitles(List.of(title));

        Review review = new Review();
        review.setReviewId(202);
        review.setContent("Good show!");
        review.setUser(user);
        review.setTitle(title);
        user.setReviews(List.of(review));

        UserProfileDTO dto = userProfileConverter.toProfileDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(1);
        assertThat(dto.getUsername()).isEqualTo("username_test");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
        assertThat(dto.getDescription()).isEqualTo("This is a bio");
        assertThat(dto.getCreateDate()).isEqualTo(LocalDate.of(2026, 6, 3));
        
        assertThat(dto.getFavoriteTitles()).hasSize(1);
        assertThat(dto.getFavoriteTitles().get(0).getWatchmodeId()).isEqualTo(101);
        
        assertThat(dto.getReviews()).hasSize(1);
        assertThat(dto.getReviews().get(0).getReviewId()).isEqualTo(202);
        assertThat(dto.getReviews().get(0).getUsername()).isEqualTo("username_test");
    }

    @Test
    public void toProfileDto_NullInput_ReturnsNull() {
        assertThat(userProfileConverter.toProfileDto(null)).isNull();
    }
}
