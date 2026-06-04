package docuTag.domain.document.dto;

import docuTag.domain.document.entity.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DocumentWithTagsDto {

    private final Long          documentId;
    private final Long          userId;
    private final String        title;
    private final String        content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<String>  tags;

    public DocumentWithTagsDto(Long documentId, Long userId,String title, String content,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.documentId = documentId;
        this.userId     = userId;
        this.title      = title;
        this.content    = content;
        this.createdAt  = createdAt;
        this.updatedAt  = updatedAt;
        this.tags   = new ArrayList<>();
    }

    public DocumentWithTagsDto(Long documentId, Long userId,String title, String content,
                               LocalDateTime createdAt, LocalDateTime updatedAt,
                               String tagNamesRaw) {
        this.documentId = documentId;
        this.userId     = userId;
        this.title      = title;
        this.content    = content;
        this.createdAt  = createdAt;
        this.updatedAt  = updatedAt;
        this.tags   = tagNamesRaw != null
                ? Arrays.asList(tagNamesRaw.split(","))
                : new ArrayList<>();
    }

    public void addTagName(String tagName) {
        this.tags.add(tagName);
    }


}
