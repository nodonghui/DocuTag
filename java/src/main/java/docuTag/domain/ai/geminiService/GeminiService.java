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
                if (!handleException(e, i, maxRetry)) {
                    return "default"; // 재시도 불필요한 예외 → 즉시 종료
                }
                // handleException이 true → 재시도 대기
            }
        }

        log.error("Gemini API 최대 재시도 초과");
        return "default";
    }

    /**
     * 예외를 분류하고 처리합니다.
     * @return true  → 재시도 가능 (잠시 대기 후 재시도)
     * @return false → 재시도 불필요 (즉시 실패 처리)
     */
    private boolean handleException(Exception e, int attempt, int maxRetry) {
        String message = e.getMessage() != null ? e.getMessage() : "";

        //재시도 가능한 예외 (여기에 계속 추가)
        if (isRetryable(message)) {
            long waitSeconds = (attempt + 1) * 5L;
            log.warn("재시도 가능한 오류 감지 [{}] — {}초 후 재시도 ({}/{})",
                    message, waitSeconds, attempt + 1, maxRetry);
            sleep(waitSeconds * 1000);
            return true;
        }

        //즉시 실패 처리할 예외 (여기에 계속 추가)
        if (isFatal(message)) {
            log.error("복구 불가능한 오류 [{}]", message);
            return false;
        }

        // 분류되지 않은 예외 → 로그 남기고 즉시 실패
        log.error("분류되지 않은 Gemini 오류: {} ({})", message, e.getClass().getSimpleName());
        return false;
    }

    /** 재시도 가능한 오류 조건 */
    private boolean isRetryable(String message) {
        return message.contains("429")   // Rate Limit
                || message.contains("503");  // 서비스 일시 불가
        // 추가 예시:
        // || message.contains("500")   // 서버 내부 오류
        // || message.contains("UNAVAILABLE")
    }

    /** 즉시 실패 처리할 오류 조건 */
    private boolean isFatal(String message) {
        return message.contains("403")   // API 키 문제
                || message.contains("400");  // 잘못된 요청
        // 추가 예시:
        // || message.contains("404")   // 모델명 오류
        // || message.contains("API_KEY_INVALID")
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
