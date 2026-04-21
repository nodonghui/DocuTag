package docuTag.global.exception;

// exception/KakaoApiException.java
public class KakaoApiException extends RuntimeException {
    private final int httpStatus;
    private final int kakaoCode;

    public KakaoApiException(String message, int httpStatus, int kakaoCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.kakaoCode = kakaoCode;
    }
    public int getHttpStatus() { return httpStatus; }
    public int getKakaoCode() { return kakaoCode; }
}
