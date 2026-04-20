package docuTag.domain.document.service;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.dto.DocumentUpdateRequest;
import docuTag.domain.document.entity.Document;
import docuTag.domain.document.entity.DocumentTag;
import docuTag.domain.document.repository.DocumentRepository;
import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.service.TagService;
import docuTag.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public DocumentSearchResponse getDocuments(List<String> tags, String title, Long lastId, int size) {
        // 1. 페이징/필터링용 (id만 뽑기)
        List<Document> documents = fetchDocuments(tags, title, lastId, size + 1);

        boolean hasNext = documents.size() == size + 1;
        if (hasNext) {
            documents = new ArrayList<>(documents.subList(0, size));
        }

        if (documents.isEmpty()) {
            return DocumentSearchResponse.of(List.of(), 0L, false);
        }

        // 2. id 목록으로 fetch join 재조회 (N+1 해결)
        List<Long> ids = documents.stream()
                .map(Document::getDocumentId)
                .toList();

        List<Document> documentsWithTags = documentRepository.findByIdsWithTags(ids);

        long nextLastId = documentsWithTags.getLast().getDocumentId();

        List<DocumentDto> documentDtos = documentsWithTags.stream()
                .map(DocumentDto::from)
                .toList();

        return DocumentSearchResponse.of(documentDtos, nextLastId, hasNext);
    }

    @Transactional(readOnly = true)
    private List<Document> fetchDocuments(List<String> tagNames,String title, Long lastId, int pageSize) {
        if (tagNames.isEmpty()) {
            return documentRepository.findDocumentsWithPaging(title,lastId, pageSize);
        }
        if (tagNames.size() == 1) {
            return documentRepository.findDocumentsByTagWithPaging(tagNames.getFirst(),title, lastId, pageSize);
        }
        return documentRepository.findDocumentsByTagsWithPaging(tagNames,title, lastId, pageSize);
    }

    // DocumentService.java
    @Transactional(readOnly = true)
    public DocumentDto getDocument(Long id, Long userId) {
        Document document = documentRepository.findByIdWithTags(id) // findById → findByIdWithTags
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id));

        if(!document.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        return DocumentDto.from(document);
    }



    @Transactional
    public void createDocument(DocumentCreateRequest request) {
        //쿠기 값으로 USER 조회/ 인가
        User user = null;
        Document document = Document.from(request, user);
        documentRepository.save(document);
        log.error("document 객체 저장 성공");

        if(request.getTags() != null) {
            request.getTags().forEach(tagName -> {
                Tag tag = tagService.findOrCreate(tagName);
                document.addTag(tag);
            });
        }
        log.error("document_tag 객체 저장 성공");
    }


    @Transactional
    public void updateDocument(Long id, DocumentUpdateRequest request) {
        // 1. 문서 조회 (DocumentTag + Tag 한 번에 fetch)
        Document document = documentRepository.findByIdWithTags(id)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다. id: " + id));

        // 2. 제목, 내용 수정
        document.updateDocument(request.getTitle(), request.getContent());

        // 3. 현재 태그를 Map으로 캐싱 (재조회 방지)
        Map<String, Tag> currentTagMap = document.getDocumentTags().stream()
                .collect(Collectors.toMap(
                        dt -> dt.getTag().getTagName(),
                        DocumentTag::getTag  // DocumentTag::getTag → 람다로 변경
                ));

        Set<String> newTagNameSet = request.getTags() != null
                ? new HashSet<>(request.getTags())
                : Collections.emptySet();

        // 4. 삭제: 이미 로딩된 객체 재사용 (DB 조회 없음)
        currentTagMap.entrySet().stream()
                .filter(e -> !newTagNameSet.contains(e.getKey()))
                .forEach(e -> document.removeTag(e.getValue())); // ← 추가 SELECT 없음

        // 5. 추가: 현재 없는 것만
        newTagNameSet.stream()
                .filter(name -> !currentTagMap.containsKey(name))
                .forEach(name -> {
                    Tag tag = tagService.findOrCreate(name);
                    document.addTag(tag);
                });
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문서를 찾을 수 없습니다. id: " + id));
        documentRepository.delete(document);
    }
}
