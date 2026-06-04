package docuTag.domain.document.dto;

import docuTag.domain.document.entity.Document;
import docuTag.global.common.PageState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DocumentSearchResponse {

    private List<DocumentDto> documents;
    private long lastId;
    private boolean hasNext;

    private DocumentSearchResponse(List<DocumentDto> documents, long lastId, boolean hasNext) {
        this.documents = documents;
        this.lastId = lastId;
        this.hasNext = hasNext;
    }

    public static DocumentSearchResponse of(List<DocumentDto> documents, long lastId, boolean hasNext) {
        return new DocumentSearchResponse(documents, lastId, hasNext);
    }

}