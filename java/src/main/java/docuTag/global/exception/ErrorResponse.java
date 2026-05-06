package docuTag.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Schema(description = "에러 응답")
public record ErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "404")
        int status,

        @Schema(description = "에러 메시지", example = "문서를 찾을 수 없습니다")
        String message,

        @Schema(description = "발생 시각")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, LocalDateTime.now());
    }
}
