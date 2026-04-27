package docuTag.domain.kakao.controller;

import docuTag.domain.kakao.service.KakaoAuthService;
import docuTag.global.exception.KakaoApiException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    // 1단계: 카카오 로그인 페이지로 리다이렉트
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        try {
            String url = kakaoAuthService.getAuthorizationUrl();
            response.sendRedirect(url);
        } catch (Exception e) {
            // 에러 발생 시 프론트 로그인 페이지로 에러코드와 함께 리다이렉트
            response.sendRedirect("http://localhost:3000/error?error=" + e.getMessage());
        }
    }

    // 2단계: 카카오가 인가 코드를 여기로 보내줌
    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        try {
            String accessToken = kakaoAuthService.getAccessToken(code);
            KakaoAuthService.KakaoUserInfo info = kakaoAuthService.getKakaoUserInfo(accessToken);
            String serviceToken = kakaoAuthService.loginOrRegister(info);
            response.sendRedirect("http://localhost:3000/?token=" + serviceToken);
        } catch (KakaoApiException e) {
            log.error("카카오 콜백 처리 실패 - kakaoCode: {}", e.getKakaoCode());
            response.sendRedirect("http://localhost:3000/error?error=" + e.getKakaoCode());
        } catch (Exception e) {
            log.error("카카오 콜백 처리 실패: {}", e.getMessage());
            response.sendRedirect("http://localhost:3000/error?error=UNKNOWN");
        }
    }
}