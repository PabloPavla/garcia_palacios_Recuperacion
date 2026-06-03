package org.vedruna.watchapi.controller.converter;

import org.junit.jupiter.api.Test;
import org.vedruna.watchapi.controller.dto.TitleDTO;
import org.vedruna.watchapi.persistance.model.Title;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TitleConverterTest {

    private final TitleConverter titleConverter = new TitleConverter();

    @Test
    public void toDto_Success() {
        Title title = new Title();
        title.setTitleId(1);
        title.setWatchmodeId(12345);
        title.setTitleName("Breaking Bad");
        title.setType("tv_series");
        title.setYear(2008);
        title.setGenre("Drama, Crime");

        TitleDTO dto = titleConverter.toDto(title);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitleId()).isEqualTo(1);
        assertThat(dto.getWatchmodeId()).isEqualTo(12345);
        assertThat(dto.getTitleName()).isEqualTo("Breaking Bad");
        assertThat(dto.getType()).isEqualTo("tv_series");
        assertThat(dto.getYear()).isEqualTo(2008);
        assertThat(dto.getGenre()).isEqualTo("Drama, Crime");
    }

    @Test
    public void toDto_NullInput_ReturnsNull() {
        assertThat(titleConverter.toDto(null)).isNull();
    }

    @Test
    public void toDtoList_Success() {
        Title title1 = new Title();
        title1.setTitleId(1);
        title1.setTitleName("Title 1");

        Title title2 = new Title();
        title2.setTitleId(2);
        title2.setTitleName("Title 2");

        List<TitleDTO> dtos = titleConverter.toDtoList(List.of(title1, title2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getTitleId()).isEqualTo(1);
        assertThat(dtos.get(0).getTitleName()).isEqualTo("Title 1");
        assertThat(dtos.get(1).getTitleId()).isEqualTo(2);
        assertThat(dtos.get(1).getTitleName()).isEqualTo("Title 2");
    }

    @Test
    public void toDtoList_NullInput_ReturnsEmptyList() {
        assertThat(titleConverter.toDtoList(null)).isEmpty();
    }
}
