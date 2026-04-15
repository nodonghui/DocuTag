package docuTag.domain.document.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateResponse {
    private Long id;
    private String message;
}