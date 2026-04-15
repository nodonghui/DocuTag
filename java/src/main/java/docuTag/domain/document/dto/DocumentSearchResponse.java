package docuTag.domain.document.dto;

import docuTag.domain.document.entity.Document;
import docuTag.global.common.PageState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DocumentSearchResponse {

    // 실제 데이터 목록
    private List<Document> documents;

    public DocumentSearchResponse(List<Document> documents) {
        this.documents = documents;
    }

    public static DocumentSearchResponse of(List<Document> documents) {
        return new DocumentSearchResponse(documents);
    }
}
