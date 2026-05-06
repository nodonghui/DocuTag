package docuTag.domain.jwt;

import docuTag.domain.jwt.refreshtoken.service.RefreshTokenService;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.repository.UserRepository;
import docuTag.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        long userId = securityUser.getUserId();

        // JWT 생성
        String jwtToken = jwtUtil.createAccessToken(userId);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        // refreshToken은 db에 저장
        // fresh api나 logout api 시 사용
        refreshTokenService.save(userId, refreshToken);

        // 쿠키 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(60 * 60)
                .build();

        // 리프레시 토큰 쿠키
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/") // refresh 엔드포인트에만 전송
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(60 * 60 * 24 * 7)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/");
    }
}
