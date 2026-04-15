package docuTag.domain.document.controller;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentSearchRequest;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.entity.Document;
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
    public ResponseEntity<DocumentSearchResponse> getDocuments(@RequestBody @Valid DocumentSearchRequest request) {
        return ResponseEntity.ok(documentService.getDocuments(request));
    }


    // 생성
    @PostMapping
    public ResponseEntity<Void> createDocument(@RequestBody @Valid DocumentCreateRequest request) {
        documentService.createDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(@PathVariable Long id,
                                                           @RequestBody @Valid DocumentRequest request) {
        return ResponseEntity.ok(documentService.updateDocument(id, request));
    }
     */

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        //documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
