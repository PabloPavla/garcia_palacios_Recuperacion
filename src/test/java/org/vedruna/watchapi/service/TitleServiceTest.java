package org.vedruna.watchapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vedruna.watchapi.controller.dto.WatchmodeSearchResponseDTO;
import org.vedruna.watchapi.controller.dto.WatchmodeTitleDetailsDTO;
import org.vedruna.watchapi.exception.BadRequestException;
import org.vedruna.watchapi.exception.ResourceNotFoundException;
import org.vedruna.watchapi.persistance.model.Title;
import org.vedruna.watchapi.persistance.model.User;
import org.vedruna.watchapi.persistance.repository.TitleRepository;
import org.vedruna.watchapi.persistance.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TitleServiceTest {

    @Mock
    private WatchmodeService watchmodeService;

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TitleService titleService;

    private User user;
    private Title title;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUserId(1);
        user.setUsername("test_user");
        user.setFavoriteTitles(new ArrayList<>());

        title = new Title();
        title.setTitleId(10);
        title.setWatchmodeId(3173903);
        title.setTitleName("Breaking Bad");
    }

    @Test
    public void searchTitles_Success() {
        WatchmodeSearchResponseDTO expected = new WatchmodeSearchResponseDTO();
        when(watchmodeService.searchTitlesByName("Breaking Bad")).thenReturn(expected);

        WatchmodeSearchResponseDTO result = titleService.searchTitles("Breaking Bad");

        assertThat(result).isEqualTo(expected);
        verify(watchmodeService, times(1)).searchTitlesByName("Breaking Bad");
    }

    @Test
    public void addFavorite_ExistingTitle_NotYetInFavorites_Success() {
        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Title result = titleService.addFavorite(3173903, user);

        assertThat(result).isEqualTo(title);
        assertThat(user.getFavoriteTitles()).contains(title);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void addFavorite_NewTitle_ResolvesFromWatchmode_Success() {
        WatchmodeTitleDetailsDTO details = new WatchmodeTitleDetailsDTO();
        details.setId(3173903);
        details.setTitle("Breaking Bad");
        details.setType("tv_series");
        details.setYear(2008);
        details.setGenreNames(List.of("Drama", "Crime"));

        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.empty());
        when(watchmodeService.getTitleDetails(3173903)).thenReturn(details);
        when(titleRepository.save(any(Title.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Title result = titleService.addFavorite(3173903, user);

        assertThat(result.getWatchmodeId()).isEqualTo(3173903);
        assertThat(result.getTitleName()).isEqualTo("Breaking Bad");
        assertThat(result.getGenre()).isEqualTo("Drama, Crime");
        assertThat(user.getFavoriteTitles()).contains(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void addFavorite_AlreadyInFavorites_ThrowsException() {
        user.getFavoriteTitles().add(title);

        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> titleService.addFavorite(3173903, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El título ya está en tu lista de favoritos");
    }

    @Test
    public void addFavorite_UserNotFound_ThrowsException() {
        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> titleService.addFavorite(3173903, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID 1");
    }

    @Test
    public void deleteFavorite_Success() {
        user.getFavoriteTitles().add(title);

        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        titleService.deleteFavorite(3173903, user);

        assertThat(user.getFavoriteTitles()).doesNotContain(title);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void deleteFavorite_TitleNotFound_ThrowsException() {
        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> titleService.deleteFavorite(3173903, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("El título con Watchmode ID 3173903 no existe en favoritos.");
    }

    @Test
    public void deleteFavorite_UserNotFound_ThrowsException() {
        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> titleService.deleteFavorite(3173903, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID 1");
    }

    @Test
    public void deleteFavorite_NotFavoritedByThisUser_ThrowsException() {
        when(titleRepository.findByWatchmodeId(3173903)).thenReturn(Optional.of(title));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> titleService.deleteFavorite(3173903, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("El título no se encuentra en tu lista de favoritos");
    }

    @Test
    public void getFavoriteTitles_Success() {
        user.getFavoriteTitles().add(title);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        List<Title> result = titleService.getFavoriteTitles(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(title);
    }

    @Test
    public void getFavoriteTitles_UserNotFound_ThrowsException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> titleService.getFavoriteTitles(user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con ID 1");
    }
}
