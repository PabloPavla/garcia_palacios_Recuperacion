package org.vedruna.watchapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.controller.dto.WatchmodeTitleDetailsDTO;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WatchmodeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private WatchmodeService watchmodeService;

    @BeforeEach
    public void setUp() {
        watchmodeService = new WatchmodeService(restTemplate, "my_api_key", "http://mybaseurl.com");
    }

    @Test
    public void searchTitlesByName_Success() {
        WatchmodeSearchResponseDTO expectedResponse = new WatchmodeSearchResponseDTO();
        expectedResponse.setResults(List.of());

        when(restTemplate.getForObject(
                eq("http://mybaseurl.com/search/?apiKey={apiKey}&search_field=name&search_value={name}"),
                eq(WatchmodeSearchResponseDTO.class),
                eq("my_api_key"),
                eq("Breaking Bad")
        )).thenReturn(expectedResponse);

        WatchmodeSearchResponseDTO result = watchmodeService.searchTitlesByName("Breaking Bad");

        assertThat(result).isNotNull();
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    public void searchTitlesByName_EmptyName_ThrowsException() {
        assertThatThrownBy(() -> watchmodeService.searchTitlesByName(""))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El parámetro de búsqueda 'name' no puede estar vacío");
    }

    @Test
    public void searchTitlesByName_NullResponse_ReturnsEmptyDTO() {
        when(restTemplate.getForObject(anyString(), eq(WatchmodeSearchResponseDTO.class), anyString(), anyString()))
                .thenReturn(null);

        WatchmodeSearchResponseDTO result = watchmodeService.searchTitlesByName("Breaking Bad");

        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNull();
    }

    @Test
    public void searchTitlesByName_ApiException_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(WatchmodeSearchResponseDTO.class), anyString(), anyString()))
                .thenThrow(new RuntimeException("API error"));

        assertThatThrownBy(() -> watchmodeService.searchTitlesByName("Breaking Bad"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Error al conectar con el servicio externo de Watchmode");
    }

    @Test
    public void getTitleDetails_Success() {
        WatchmodeTitleDetailsDTO expectedDetails = new WatchmodeTitleDetailsDTO();
        expectedDetails.setId(3173903);
        expectedDetails.setTitle("Breaking Bad");

        when(restTemplate.getForObject(
                eq("http://mybaseurl.com/title/{watchmodeId}/details/?apiKey={apiKey}"),
                eq(WatchmodeTitleDetailsDTO.class),
                eq(3173903),
                eq("my_api_key")
        )).thenReturn(expectedDetails);

        WatchmodeTitleDetailsDTO result = watchmodeService.getTitleDetails(3173903);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3173903);
        assertThat(result.getTitle()).isEqualTo("Breaking Bad");
    }

    @Test
    public void getTitleDetails_NullId_ThrowsException() {
        assertThatThrownBy(() -> watchmodeService.getTitleDetails(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El ID de Watchmode es requerido");
    }

    @Test
    public void getTitleDetails_NullResponse_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(WatchmodeTitleDetailsDTO.class), anyInt(), anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> watchmodeService.getTitleDetails(3173903))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se encontró el título en la API externa de Watchmode");
    }

    @Test
    public void getTitleDetails_ApiException_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(WatchmodeTitleDetailsDTO.class), anyInt(), anyString()))
                .thenThrow(new RuntimeException("Connection timeout"));

        assertThatThrownBy(() -> watchmodeService.getTitleDetails(3173903))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No se pudo obtener información del título desde Watchmode");
    }
}
