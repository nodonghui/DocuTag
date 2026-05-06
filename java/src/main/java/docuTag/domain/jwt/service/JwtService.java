package docuTag.domain.jwt.service;

import docuTag.domain.jwt.refreshtoken.entity.RefreshToken;
import docuTag.domain.jwt.refreshtoken.service.RefreshTokenService;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.service.UserService;
import docuTag.global.exception.ServiceException;
import docuTag.global.util.JwtUtil;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final BlackListService blackListService;

    /**
     * refreshToken 검증 후 새 accessToken 쿠키 반환
     */
    public ResponseCookie refreshAccessToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        RefreshToken rt = refreshTokenService.findByToken(refreshToken);
        User user = userService.findById(rt.getUserId());

        String newAccessToken = jwtUtil.createAccessToken(user.getUserId());

        return buildAccessTokenCookie(newAccessToken);
    }

    /**
     * accessToken + refreshToken 검증 후 userId 반환
     */
    public long validateAndGetUserId(String accessToken, String refreshToken) {
        validateAccessToken(accessToken);
        validateRefreshToken(refreshToken);
        validateNotBlacklisted(accessToken);
        validateRefreshTokenExists(refreshToken); // DB에 존재하는지

        return jwtUtil.getUserId(accessToken);
    }

    // --- private 분리 ---

    private void validateAccessToken(String accessToken) {
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            throw new ServiceException(401, "유효하지 않은 액세스 토큰입니다.");
        }
    }


    private void validateNotBlacklisted(String accessToken) {
        if (blackListService.isBlacklisted(accessToken)) {
            throw new ServiceException(401, "블랙리스트에 등록된 토큰입니다.");
        }
    }

    private void validateRefreshTokenExists(String refreshToken) {
        refreshTokenService.findByToken(refreshToken); // 없으면 내부에서 예외 던짐
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new ServiceException(401, "리프레시 토큰이 없습니다.");
        }
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ServiceException(401, "유효하지 않은 리프레시 토큰입니다.");
        }
    }

    private ResponseCookie buildAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(60 * 60)
                .build();
    }
}
