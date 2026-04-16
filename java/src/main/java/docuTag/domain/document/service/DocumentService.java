package docuTag.domain.document.service;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.dto.DocumentUpdateRequest;
import docuTag.domain.document.entity.Document;
import docuTag.domain.document.repository.DocumentRepository;
import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.service.TagService;
import docuTag.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    public DocumentSearchResponse getDocuments(List<String> tags,String title, Long lastId, int size) {
        log.error("tags : {}, title : {}, lastId : {}, pageSize : {}",tags,title,lastId,size);
        List<Document> documents = fetchDocuments(tags,title,lastId,size+1);

        boolean hasNext = documents.size() == size + 1;
        if (hasNext) {
            documents = new ArrayList<>(documents.subList(0, size));
        }

        long nextLastId = 0L;
        if (!documents.isEmpty()) {
            nextLastId = documents.getLast().getDocumentId();
        }


        List<DocumentDto> documentDtos = documents.stream()
                .map(DocumentDto::from)
                .toList();

        log.error("nextLastId : {}, hasNext : {}",nextLastId,hasNext);

        return DocumentSearchResponse.of(documentDtos, nextLastId, hasNext);

    }

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
    public DocumentDto getDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id));

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
        // 1. 문서 조회
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다. id: " + id));

        // 2. 제목, 내용 수정
        document.updateDocument(request.getTitle(), request.getContent());

        // 3. 태그 비교 및 동기화
        List<String> newTagNames = request.getTags() != null ? request.getTags() : List.of();

        // 현재 연결된 태그 이름 목록
        Set<String> currentTagNames = document.getDocumentTags().stream()
                .map(dt -> dt.getTag().getTagName())
                .collect(Collectors.toSet());

        Set<String> newTagNameSet = new HashSet<>(newTagNames);

        // 삭제할 태그: 현재 있는데 새 목록에 없는 것
        Set<String> toRemove = currentTagNames.stream()
                .filter(name -> !newTagNameSet.contains(name))
                .collect(Collectors.toSet());

        // 추가할 태그: 새 목록에 있는데 현재 없는 것
        Set<String> toAdd = newTagNameSet.stream()
                .filter(name -> !currentTagNames.contains(name))
                .collect(Collectors.toSet());

        // 삭제
        toRemove.forEach(tagName -> {
            Tag tag = tagService.findByTagName(tagName);
            document.removeTag(tag);
        });

        // 추가 (없으면 생성)
        toAdd.forEach(tagName -> {
            Tag tag = tagService.findOrCreate(tagName);
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
