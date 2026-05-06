package docuTag.domain.ai.controller;

import docuTag.domain.ai.dto.GeminiRequest;
import docuTag.domain.ai.geminiService.GeminiService;
import docuTag.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/summarize")
    @Operation(summary = "문서 요약", description = "Gemini AI를 이용해 문서 내용을 요약합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요약 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "요약된 내용입니다.")
                    )),
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
            @ApiResponse(responseCode = "502", description = "Gemini API 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "502Example",
                                    ref = "#/components/examples/502Example"
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
    public ResponseEntity<String> summarize(@RequestBody GeminiRequest request) {
        String result = "default";
        result = geminiService.summarize(request.getContent(),request.getMode());
        return ResponseEntity.ok(result);
    }
}