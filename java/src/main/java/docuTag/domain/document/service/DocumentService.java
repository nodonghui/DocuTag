package docuTag.domain.document.service;

import docuTag.domain.document.dto.*;
import docuTag.domain.document.entity.Document;
import docuTag.domain.document.entity.DocumentTag;
import docuTag.domain.document.repository.DocumentRepository;
import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.service.TagService;
import docuTag.domain.user.entity.User;

import docuTag.domain.user.repository.UserRepository;
import docuTag.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TagService tagService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DocumentSearchResponse getDocuments(List<String> tags, String title, Long lastId, int size, Long userId) {
        List<Document> documents = fetchDocuments(tags, title, lastId, size + 1, userId);

        boolean hasNext = documents.size() == size + 1;
        if (hasNext) {
            documents = new ArrayList<>(documents.subList(0, size));
        }

        if (documents.isEmpty()) {
            return DocumentSearchResponse.of(List.of(), 0L, false);
        }

        List<Long> ids = documents.stream()
                .map(Document::getDocumentId)
                .toList();

        List<Document> documentsWithTags = documentRepository.findByIdsWithTags(ids, userId);

        long nextLastId = documentsWithTags.getLast().getDocumentId();

        List<DocumentDto> documentDtos = documentsWithTags.stream()
                .map(DocumentDto::from)
                .toList();

        return DocumentSearchResponse.of(documentDtos, nextLastId, hasNext);
    }


    @Transactional(readOnly = true)
    public DocumentSearchResponse2 getDocuments2(List<String> tags, String title, Long lastId, int size, Long userId) {

        List<Long> ids = tags.isEmpty()
                ? documentRepository.findDocumentIdsWithPaging(userId, title, lastId, size + 1)
                : documentRepository.findDocumentIdsByTagNamesPaging(userId, lastId, title, tags, size);

        if (ids.isEmpty()) {
            return DocumentSearchResponse2.of(List.of(), 0L, false);
        }

        boolean hasNext = ids.size() == size + 1;

        if (hasNext) {
            ids = new ArrayList<>(ids.subList(0, size));
        }

        List<Document> documents = documentRepository.findAllWithTags(ids);

        List<DocumentWithTagsDto> dtos = documents.stream()
                .map(doc -> {
                    DocumentWithTagsDto dto = new DocumentWithTagsDto(
                            doc.getDocumentId(),
                            doc.getUser().getUserId(),
                            doc.getTitle(),
                            doc.getContent(),
                            doc.getCreatedAt(),
                            doc.getUpdatedAt()
                    );
                    doc.getDocumentTags().forEach(dt ->
                            dto.addTagName(dt.getTag().getTagName())
                    );
                    return dto;
                })
                .toList();

        long nextLastId = documents.getLast().getDocumentId();

        return DocumentSearchResponse2.of(dtos, nextLastId, hasNext);
    }




    private List<Document> fetchDocuments(List<String> tagNames, String title, Long lastId, int pageSize, Long userId) {
        if (tagNames.isEmpty()) {
            return documentRepository.findDocumentsWithPaging(userId, title, lastId, pageSize);
        }

        return documentRepository.findDocumentsByTagsWithPaging(userId, tagNames, title, lastId, pageSize);
    }

    public List<DocumentWithTagsDto> getFilteredDocumentsWithTags(
            Long         userId,
            Long         lastId,
            String       title,
            List<String> tagNames,
            int          pageSize
    ) {
        List<Object[]> rows = documentRepository.findFilteredWithTags(
                userId, lastId, title, tagNames, pageSize
        );

        Map<Long, DocumentWithTagsDto> dtoMap = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Long documentId = ((Number) row[0]).longValue();

            dtoMap.computeIfAbsent(documentId, id -> new DocumentWithTagsDto(
                    id,
                    ((Number)    row[1]).longValue(),           // user_id
                    (String)     row[2],                        // title
                    (String)     row[3],                        // content
                    toLocalDateTime(row[4]),                    // created_at
                    toLocalDateTime(row[5])                     // updated_at
            ));

            dtoMap.get(documentId).addTagName((String) row[6]); // tag_name
        }

        return new ArrayList<>(dtoMap.values());
    }

    public List<DocumentWithTagsDto> getFilteredDocumentsWithRawTags(
            Long         userId,
            Long         lastId,
            String       title,
            List<String> tagNames,
            int          pageSize
    ) {
        List<Object[]> rows = documentRepository.findFilteredWithTagsGrouped(
                userId, lastId, title, tagNames, pageSize
        );

        return rows.stream()
                .map(row -> new DocumentWithTagsDto(
                        ((Number)    row[0]).longValue(),   // document_id
                        ((Number)    row[1]).longValue(),   // user_id
                        (String)     row[2],                // title
                        (String)     row[3],                // content
                        toLocalDateTime(row[4]),            // created_at
                        toLocalDateTime(row[5]),            // updated_at
                        (String)     row[6]                 // tag_names (GROUP_CONCAT 결과)
                ))
                .collect(Collectors.toList());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof Timestamp ts) return ts.toLocalDateTime();
        if (value instanceof LocalDateTime ldt) return ldt;
        return null;
    }

    @Transactional(readOnly = true)
    public DocumentDto getDocument(Long id, Long userId) {
        // 1. 문서 존재 여부 확인 (userId 없이 조회)
        Document document = documentRepository.findByIdWithTags(id)
                .orElseThrow(() -> new ServiceException(404, "Document not found: " + id));

        Long documentUserId = document.getUser().getUserId();
        if(!userId.equals(documentUserId)) {
            throw new ServiceException(403, "Document access denied: " + id);
        }

        return DocumentDto.from(document);
    }

    @Transactional
    public void createDocument(DocumentCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        Document document = Document.from(request, user);
        documentRepository.save(document);
        log.info("document 객체 저장 성공");

        if (request.getTags() != null) {
            request.getTags().forEach(tagName -> {
                Tag tag = tagService.findOrCreate(tagName);
                document.addTag(tag);
            });
        }
        log.info("document_tag 객체 저장 성공");
    }

    @Transactional
    public void updateDocument(Long id, DocumentUpdateRequest request, Long userId) {

        // 1. 문서 존재 여부 확인 (userId 없이 조회)
        Document document = documentRepository.findByIdWithTags(id)
                .orElseThrow(() -> new ServiceException(404, "Document not found: " + id));

        Long documentUserId = document.getUser().getUserId();
        if(!userId.equals(documentUserId)) {
            throw new ServiceException(403, "Document access denied: " + id);
        }


        document.updateDocument(request.getTitle(), request.getContent());

        Map<String, Tag> currentTagMap = document.getDocumentTags().stream()
                .collect(Collectors.toMap(
                        dt -> dt.getTag().getTagName(),
                        DocumentTag::getTag
                ));

        Set<String> newTagNameSet = request.getTags() != null
                ? new HashSet<>(request.getTags())
                : Collections.emptySet();

        currentTagMap.entrySet().stream()
                .filter(e -> !newTagNameSet.contains(e.getKey()))
                .forEach(e -> document.removeTag(e.getValue()));

        newTagNameSet.stream()
                .filter(name -> !currentTagMap.containsKey(name))
                .forEach(name -> {
                    Tag tag = tagService.findOrCreate(name);
                    document.addTag(tag);
                });
    }

    @Transactional
    public void deleteDocument(Long id, Long userId) {
        // 1. 문서 존재 여부 확인 (userId 없이 조회)
        Document document = documentRepository.findByIdWithTags(id)
                .orElseThrow(() -> new ServiceException(404, "Document not found: " + id));

        Long documentUserId = document.getUser().getUserId();
        if(!userId.equals(documentUserId)) {
            throw new ServiceException(403, "Document access denied: " + id);
        }
        documentRepository.delete(document);
    }
}
