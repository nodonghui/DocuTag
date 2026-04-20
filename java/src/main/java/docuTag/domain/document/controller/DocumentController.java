package docuTag.domain.document.controller;

import docuTag.domain.document.dto.DocumentCreateRequest;
import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentSearchResponse;
import docuTag.domain.document.dto.DocumentUpdateRequest;
import docuTag.domain.document.service.DocumentService;
import docuTag.global.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    private Long extractUserId(String authHeader) {
        return jwtUtil.getUserId(authHeader.replace("Bearer ", ""));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<DocumentSearchResponse> getDocuments(
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader
    ) {
        if (tags == null) tags = List.of();
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(documentService.getDocuments(tags, title, lastId, size, userId));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(documentService.getDocument(id, userId));
    }

    // 생성
    @PostMapping
    public ResponseEntity<Void> createDocument(
            @RequestBody @Valid DocumentCreateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        documentService.createDocument(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDocument(
            @PathVariable Long id,
            @RequestBody @Valid DocumentUpdateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        documentService.updateDocument(id, request, userId);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        documentService.deleteDocument(id, userId);
        return ResponseEntity.noContent().build();
    }
}
