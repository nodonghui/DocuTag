package docuTag.domain.ai.dto;

import docuTag.domain.ai.SummaryMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeminiRequest {
    @NotBlank
    private String content;

    SummaryMode mode;
}