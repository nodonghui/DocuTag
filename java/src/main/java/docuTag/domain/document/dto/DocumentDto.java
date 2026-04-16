package docuTag.domain.document.dto;

import docuTag.domain.document.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// DocumentDto.java
@Getter
@AllArgsConstructor
public class DocumentDto {
    private Long documentId;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DocumentDto from(Document document) {
        List<String> tagNames = document.getDocumentTags().stream()
                .map(dt -> dt.getTag().getTagName())
                .toList();
        return new DocumentDto(document.getDocumentId(), document.getTitle(), document.getContent(), tagNames
                ,document.getCreatedAt(),document.getUpdatedAt());
    }
}