package docuTag.domain.ai.controller;

import docuTag.domain.ai.dto.GeminiRequest;
import docuTag.domain.ai.geminiService.GeminiService;
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
    public ResponseEntity<String> summarize(@RequestBody GeminiRequest request) {
        String result = "default";
        result = geminiService.summarize(request.getContent(),request.getMode());
        return ResponseEntity.ok(result);
    }
}