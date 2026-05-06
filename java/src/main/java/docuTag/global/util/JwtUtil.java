package docuTag.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long ACCESS_EXPIRATION = 1000 * 60 * 60;          // 1시간
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    // userId 추출
    public Long getUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    // 토큰 타입 추출 (access / refresh)
    public String getTokenType(String token) {
        return (String) getClaims(token).get("type");
    }

    // 만료시간 추출
    public LocalDateTime getExpiresAt(String token) {
        return getClaims(token)
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 토큰: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 토큰: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("서명 검증 실패: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("토큰이 비어있음: {}", e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}