package docuTag.domain.ai.geminiService;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import docuTag.domain.ai.SummaryMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    // instruction을 Map으로 저장
    private static final Map<SummaryMode, String> INSTRUCTION_MAP = Arrays.stream(SummaryMode.values())
            .collect(Collectors.toMap(
                    mode -> mode,
                    SummaryMode::getInstruction
            ));

    public String summarize(String content, SummaryMode mode) {
        String instruction = INSTRUCTION_MAP.getOrDefault(mode, "텍스트를 문단 형식으로 정리해줘.");

        int maxRetry = 3;
        for (int i = 0; i < maxRetry; i++) {
            try (Client client = Client.builder().apiKey(apiKey).build()) {
                GenerateContentConfig config = GenerateContentConfig.builder()
                        .systemInstruction(Content.fromParts(Part.fromText(instruction)))
                        .build();

                GenerateContentResponse response =
                        client.models.generateContent("gemini-2.5-flash", content, config);

                return response == null ? "default" : response.text();

            } catch (Exception e) {
                if ( !(e.getMessage().contains("429") || e.getMessage().contains("503")) ) {
                    log.error("Gemini API 호출 실패: {}", e.getMessage());
                    return "default";
                }
                log.warn("429 감지 — {}초 후 재시도 ({}/{})", (i + 1) * 5, i + 1, maxRetry);
                try { Thread.sleep(5000L * (i + 1)); }
                catch (InterruptedException ignored) {}
            }
        }
        return "default";
    }
}
