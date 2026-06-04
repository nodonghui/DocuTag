package docuTag.domain.document.controller;

import docuTag.domain.document.dto.*;
import docuTag.domain.document.service.DocumentService;
import docuTag.domain.jwt.SecurityUser;
import docuTag.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document", description = "문서 관리 API")
public class DocumentController {

    private final DocumentService documentService;

    // 전체 조회
    @GetMapping
    @Operation(summary = "문서 목록 조회", description = "태그/제목 기반 커서 페이지네이션 문서 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DocumentSearchResponse.class)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "400Example",
                                    ref = "#/components/examples/400Example"
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "403Example",
                                    ref = "#/components/examples/403Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<DocumentSearchResponse2> getDocuments(
            @Parameter(description = "태그 목록 (복수 가능)") @RequestParam(required = false) List<String> tags,
            @Parameter(description = "제목 검색어") @RequestParam(required = false) String title,
            @Parameter(description = "커서 ID (이전 페이지 마지막 ID)") @RequestParam(required = false) Long lastId,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (tags == null) tags = List.of();
        Long userId = securityUser.getUserId();
        return ResponseEntity.ok(documentService.getDocuments2(tags, title, lastId, size, userId));
    }

    // 단건 조회
    @GetMapping("/{id}")
    @Operation(summary = "문서 단건 조회", description = "문서 ID로 단건 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DocumentDto.class)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "403Example",
                                    ref = "#/components/examples/403Example"
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "404Example",
                                    ref = "#/components/examples/404Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<DocumentDto> getDocument(
            @Parameter(description = "문서 ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUserId();
        return ResponseEntity.ok(documentService.getDocument(id, userId));
    }

    // 생성
    @PostMapping
    @Operation(summary = "문서 생성", description = "새 문서를 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "400Example",
                                    ref = "#/components/examples/400Example"
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<Void> createDocument(
            @RequestBody @Valid DocumentCreateRequest request,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUserId();
        documentService.createDocument(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 수정
    @PutMapping("/{id}")
    @Operation(summary = "문서 수정", description = "문서 ID로 문서를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "400Example",
                                    ref = "#/components/examples/400Example"
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "403Example",
                                    ref = "#/components/examples/403Example"
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "404Example",
                                    ref = "#/components/examples/404Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<Void> updateDocument(
            @Parameter(description = "문서 ID", required = true) @PathVariable Long id,
            @RequestBody @Valid DocumentUpdateRequest request,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUserId();
        documentService.updateDocument(id, request, userId);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "문서 삭제", description = "문서 ID로 문서를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "401Example",
                                    ref = "#/components/examples/401Example"
                            )
                    )),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "403Example",
                                    ref = "#/components/examples/403Example"
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "문서를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "404Example",
                                    ref = "#/components/examples/404Example"
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "500Example",
                                    ref = "#/components/examples/500Example"
                            )
                    ))
    })
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "문서 ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Long userId = securityUser.getUserId();
        documentService.deleteDocument(id, userId);
        return ResponseEntity.noContent().build();
    }
}
