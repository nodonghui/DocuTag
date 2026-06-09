package docuTag.domain.document.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentWithTagsDtoTest {

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("addTagName - tags 리스트에 순서대로 누적")
    void addTagName_accumulatesTags() {
        DocumentWithTagsDto dto = new DocumentWithTagsDto(1L, 1L, "제목", "내용", now, now);

        dto.addTagName("java");
        dto.addTagName("spring");

        assertThat(dto.getTags()).containsExactly("java", "spring");
    }

    @Test
    @DisplayName("두 번째 생성자 - tagNamesRaw 쉼표로 분리")
    void constructor_withRawTags_splitsByComma() {
        DocumentWithTagsDto dto = new DocumentWithTagsDto(1L, 1L, "제목", "내용", now, now, "java,spring,jpa");

        assertThat(dto.getTags()).containsExactly("java", "spring", "jpa");
    }

    @Test
    @DisplayName("두 번째 생성자 - tagNamesRaw null이면 빈 리스트")
    void constructor_withNullRawTags_returnsEmptyList() {
        DocumentWithTagsDto dto = new DocumentWithTagsDto(1L, 1L, "제목", "내용", now, now, null);

        assertThat(dto.getTags()).isEmpty();
    }
}
