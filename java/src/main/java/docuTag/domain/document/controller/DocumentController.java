package docuTag.domain.document.controller;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.dto.DocumentUpdateRequest;
import docuTag.domain.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;


    // 전체 조회
    @GetMapping
    public ResponseEntity<DocumentSearchResponse> getDocuments(
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size
    ) {
        if(tags == null) tags = List.of();

        return ResponseEntity.ok(documentService.getDocuments(tags,title, lastId, size));
    }

    // DocumentController.java
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        DocumentDto documentResponse = documentService.getDocument(id);
        return ResponseEntity.ok(documentResponse);
    }


    // 생성
    @PostMapping
    public ResponseEntity<Void> createDocument(@RequestBody @Valid DocumentCreateRequest request) {
        documentService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long id,
                                                           @RequestBody @Valid DocumentUpdateRequest request) {
        documentService.updateDocument(id,request);
        return ResponseEntity.ok().build();
    }


    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
