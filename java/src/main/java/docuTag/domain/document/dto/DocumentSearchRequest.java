package docuTag.domain.document.dto;

import docuTag.global.common.PageState;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DocumentSearchRequest {
    private List<@NotBlank String> tags;
    private PageState pageState;
}
