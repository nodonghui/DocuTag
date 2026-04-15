package docuTag.domain.document.service;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentSearchRequest;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.entity.Document;
import docuTag.domain.document.repository.DocumentRepository;
import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.service.TagService;
import docuTag.domain.user.entity.User;
import docuTag.global.common.PageState;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TagService tagService;


    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    public DocumentSearchResponse getDocuments(DocumentSearchRequest request) {

        PageState pageState = request.getPageState();
        List<Document> documents = fetchDocuments(request.getTags(), pageState.getLastId(), pageState.getSize());

        return DocumentSearchResponse.of(documents);

    }

    private List<Document> fetchDocuments(List<String> tagNames, long lastId, int pageSize) {
        if (tagNames.isEmpty()) {
            return documentRepository.findDocumentsWithPaging(lastId, pageSize);
        }
        if (tagNames.size() == 1) {
            return documentRepository.findDocumentsByTagWithPaging(tagNames.getFirst(), lastId, pageSize);
        }
        return documentRepository.findDocumentsByTagsWithPaging(tagNames, lastId, pageSize);
    }

    public void createDocument(DocumentCreateRequest request) {
        //쿠기 값으로 USER 조회/ 인가
        User user = null;
        Document document = Document.from(request, user);
        documentRepository.save(document);

        if(request.getTags() != null) {
            request.getTags().forEach(tagName -> {
                Tag tag = tagService.findOrCreate(tagName);
                document.addTag(tag);
            });
        }
    }
}
