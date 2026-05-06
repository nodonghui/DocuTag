package docuTag.global.exception;



import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 비즈니스 예외 (에러 코드 유지)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(ServiceException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorResponse.of(e.getStatusCode(), e.getMessage()));
    }


    // 파라미터 바인딩 에러 (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, message));
    }

    // 내부 서버 에러 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "서버 내부 오류가 발생했습니다"));
    }

    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(GeminiApiException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)   // 502
                .body(ErrorResponse.of(502, e.getMessage()));
    }
}
