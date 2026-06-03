package org.vedruna.watchapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vedruna.watchapi.controller.dto.ReviewEditRequestDTO;
import org.vedruna.watchapi.controller.dto.ReviewRequestDTO;
import org.vedruna.watchapi.controller.dto.WatchmodeTitleDetailsDTO;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.Review;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.ReviewRepository;
import org.vedruna.watchapi.persistance.repository.TitleRepository;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WatchmodeService watchmodeService;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Title title;
    private Review review;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1);
        user.setUsername("test_user");

        title = new Title();
        title.setTitleId(10);
        title.setWatchmodeId(3173903);
        title.setTitleName("Breaking Bad");

        review = new Review();
        review.setReviewId(20);
        review.setContent("Awesome show!");
        review.setRating(10);
        review.setUser(user);
        review.setTitle(title);
    }

    @Test
    public void getReviewsByTitle_Success() {
        when(reviewRepository.findByTitleWatchmodeId(3173903)).thenReturn(List.of(review));

        List<Review> result = reviewService.getReviewsByTitle(3173903);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(review);
    }

    @Test
    public void createReview_ExistingTitle_Success() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setWatchmodeId(3173903);
        request.setContent("Very good!");
        request.setRating(9);

        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.createReview(request, user);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Very good!");
        assertThat(result.getRating()).isEqualTo(9);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTitle()).isEqualTo(title);
    }

    @Test
    public void createReview_NewTitle_ResolvesFromWatchmode_Success() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setWatchmodeId(3173903);
        request.setContent("Very good!");
        request.setRating(9);

        WatchmodeTitleDetailsDTO details = new WatchmodeTitleDetailsDTO();
        details.setId(3173903);
        details.setTitle("Breaking Bad");
        details.setType("tv_series");
        details.setYear(2008);
        details.setGenreNames(List.of("Drama"));

        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.empty());
        when(watchmodeService.getTitleDetails(3173903)).thenReturn(details);
        when(titleRepository.save(any(Title.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.createReview(request, user);

        assertThat(result).isNotNull();
        assertThat(result.getTitle().getTitleName()).isEqualTo("Breaking Bad");
        verify(titleRepository, times(1)).save(any(Title.class));
    }

    @Test
    public void createReview_UserNotFound_ThrowsException() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setWatchmodeId(3173903);

        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID 1");
    }

    @Test
    public void updateReview_Success() {
        ReviewEditRequestDTO request = new ReviewEditRequestDTO();
        request.setContent("Updated content");
        request.setRating(8);

        when(reviewRepository.findById(20)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.updateReview(20, request, user);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated content");
        assertThat(result.getRating()).isEqualTo(8);
        assertThat(result.getEditDate()).isNotNull();
    }

    @Test
    public void updateReview_NotFound_ThrowsException() {
        ReviewEditRequestDTO request = new ReviewEditRequestDTO();
        when(reviewRepository.findById(20)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(20, request, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reseña no encontrada con ID 20");
    }

    @Test
    public void updateReview_NotOwner_ThrowsException() {
        User otherUser = new User();
        otherUser.setUserId(2);
        otherUser.setUsername("other");

        ReviewEditRequestDTO request = new ReviewEditRequestDTO();

        when(reviewRepository.findById(20)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(20, request, otherUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No tienes permisos para editar esta reseña porque no te pertenece.");
    }

    @Test
    public void deleteReview_Success() {
        when(reviewRepository.findById(20)).thenReturn(Optional.of(review));

        reviewService.deleteReview(20, user);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    public void deleteReview_NotFound_ThrowsException() {
        when(reviewRepository.findById(20)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(20, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reseña no encontrada con ID 20");
    }

    @Test
    public void deleteReview_NotOwner_ThrowsException() {
        User otherUser = new User();
        otherUser.setUserId(2);
        otherUser.setUsername("other");

        when(reviewRepository.findById(20)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(20, otherUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No tienes permisos para eliminar esta reseña porque no te pertenece.");
    }
}
