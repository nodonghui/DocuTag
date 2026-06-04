package docuTag.domain.document.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DocumentSearchResponse2 {

    private List<DocumentWithTagsDto> documents;
    private long lastId;
    private boolean hasNext;

    private DocumentSearchResponse2(List<DocumentWithTagsDto> documents, long lastId, boolean hasNext) {
        this.documents = documents;
        this.lastId = lastId;
        this.hasNext = hasNext;
    }

    public static DocumentSearchResponse2 of(List<DocumentWithTagsDto> documents, long lastId, boolean hasNext) {
        return new DocumentSearchResponse2(documents, lastId, hasNext);
    }

}