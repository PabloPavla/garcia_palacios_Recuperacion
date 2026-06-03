package org.vedruna.watchapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.vedruna.watchapi.controller.converter.ReviewConverter;
import org.vedruna.watchapi.controller.dto.ReviewDTO;
import org.vedruna.watchapi.controller.dto.ReviewEditRequestDTO;
import org.vedruna.watchapi.controller.dto.ReviewRequestDTO;
import org.vedruna.watchapi.persistance.model.Review;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.config.ApplicationConfig;
import org.vedruna.watchapi.security.config.SecurityConfig;
import org.vedruna.watchapi.security.filter.JwtAuthenticationFilter;
import org.vedruna.watchapi.service.ReviewService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ReviewController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            SecurityConfig.class,
            ApplicationConfig.class,
            JwtAuthenticationFilter.class
        }
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewConverter reviewConverter;

    @Test
    public void getReviews_Success() throws Exception {
        List<Review> reviews = Collections.singletonList(new Review());
        List<ReviewDTO> reviewDTOs = Collections.singletonList(new ReviewDTO());

        when(reviewService.getReviewsByTitle(317390)).thenReturn(reviews);
        when(reviewConverter.toDtoList(reviews)).thenReturn(reviewDTOs);

        mockMvc.perform(get("/titles/317390/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(reviewService, times(1)).getReviewsByTitle(317390);
    }

    @Test
    public void createReview_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setWatchmodeId(317390);
        request.setRating(9);
        request.setContent("Excellent!");

        Review review = new Review();
        review.setReviewId(1);

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(1);
        reviewDTO.setRating(9);
        reviewDTO.setContent("Excellent!");

        when(reviewService.createReview(any(ReviewRequestDTO.class), any(User.class))).thenReturn(review);
        when(reviewConverter.toDto(review)).thenReturn(reviewDTO);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.rating").value(9));

        verify(reviewService, times(1)).createReview(any(ReviewRequestDTO.class), any(User.class));
    }

    @Test
    public void updateReview_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        ReviewEditRequestDTO request = new ReviewEditRequestDTO();
        request.setRating(8);
        request.setContent("Updated!");

        Review review = new Review();
        review.setReviewId(1);

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(1);
        reviewDTO.setRating(8);
        reviewDTO.setContent("Updated!");

        when(reviewService.updateReview(eq(1), any(ReviewEditRequestDTO.class), any(User.class))).thenReturn(review);
        when(reviewConverter.toDto(review)).thenReturn(reviewDTO);

        mockMvc.perform(put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.rating").value(8));

        verify(reviewService, times(1)).updateReview(eq(1), any(ReviewEditRequestDTO.class), any(User.class));
    }

    @Test
    public void deleteReview_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        doNothing().when(reviewService).deleteReview(eq(1), any(User.class));

        mockMvc.perform(delete("/reviews/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(content().string("Reseña eliminada exitosamente"));

        verify(reviewService, times(1)).deleteReview(eq(1), any(User.class));
    }
}
