package docuTag.domain.jwt.refreshtoken.service;

import docuTag.domain.jwt.refreshtoken.repository.RefreshTokenRepository;
import docuTag.domain.jwt.refreshtoken.entity.RefreshToken;
import docuTag.global.exception.ServiceException;
import docuTag.global.util.JwtUtil;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;


    public void save(Long userId, String token) {
        // 기존 토큰 있으면 교체 (디바이스당 1개 유지)
        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        rt -> rt.rotate(token, jwtUtil.getExpiresAt(token)),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .userId(userId)
                                .token(token)
                                .expiresAt(jwtUtil.getExpiresAt(token))
                                .build())
                );
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ServiceException(401,"INVALID_REFRESH_TOKEN"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new ServiceException(401,"EXPIRED_REFRESH_TOKEN");
        }

        return refreshToken;
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
