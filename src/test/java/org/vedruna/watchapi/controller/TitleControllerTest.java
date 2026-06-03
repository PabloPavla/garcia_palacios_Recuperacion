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
import org.springframework.test.web.servlet.MockMvc;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.security.config.ApplicationConfig;
import org.vedruna.watchapi.security.config.SecurityConfig;
import org.vedruna.watchapi.security.filter.JwtAuthenticationFilter;
import org.vedruna.watchapi.service.TitleService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = TitleController.class,
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
public class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitleService titleService;

    @Test
    public void search_Success() throws Exception {
        WatchmodeSearchResponseDTO responseDTO = new WatchmodeSearchResponseDTO();
        responseDTO.setResults(Collections.emptyList());

        when(titleService.searchTitles("Breaking Bad")).thenReturn(responseDTO);

        mockMvc.perform(get("/titles/search")
                        .param("name", "Breaking Bad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title_results").exists());

        verify(titleService, times(1)).searchTitles("Breaking Bad");
    }
}
