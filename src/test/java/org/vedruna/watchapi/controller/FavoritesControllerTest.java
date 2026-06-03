package org.vedruna.watchapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.vedruna.watchapi.controller.converter.TitleConverter;
import org.vedruna.watchapi.controller.dto.TitleDTO;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.security.config.ApplicationConfig;
import org.vedruna.watchapi.security.config.SecurityConfig;
import org.vedruna.watchapi.security.filter.JwtAuthenticationFilter;
import org.vedruna.watchapi.service.TitleService;

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
    controllers = FavoritesController.class,
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
public class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitleService titleService;

    @MockBean
    private TitleConverter titleConverter;

    @Test
    public void addFavorite_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        Title title = new Title();
        title.setWatchmodeId(317390);

        TitleDTO titleDTO = new TitleDTO();
        titleDTO.setWatchmodeId(317390);

        when(titleService.addFavorite(eq(317390), any(User.class))).thenReturn(title);
        when(titleConverter.toDto(title)).thenReturn(titleDTO);

        mockMvc.perform(post("/favorites/317390")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.watchmodeId").value(317390));

        verify(titleService, times(1)).addFavorite(eq(317390), any(User.class));
    }

    @Test
    public void deleteFavorite_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        doNothing().when(titleService).deleteFavorite(eq(317390), any(User.class));

        mockMvc.perform(delete("/favorites/317390")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(content().string("Título eliminado de favoritos exitosamente"));

        verify(titleService, times(1)).deleteFavorite(eq(317390), any(User.class));
    }

    @Test
    public void getMyFavorites_Success() throws Exception {
        User user = new User();
        user.setUsername("test_user");

        List<Title> titles = Collections.singletonList(new Title());
        List<TitleDTO> titleDTOs = Collections.singletonList(new TitleDTO());

        when(titleService.getFavoriteTitles(any(User.class))).thenReturn(titles);
        when(titleConverter.toDtoList(titles)).thenReturn(titleDTOs);

        mockMvc.perform(get("/favorites/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(titleService, times(1)).getFavoriteTitles(any(User.class));
    }
}
