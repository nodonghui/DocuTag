package docuTag.domain.kakao.controller;

import docuTag.domain.kakao.service.KakaoAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    // 1단계: 카카오 로그인 페이지로 리다이렉트
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String url = kakaoAuthService.getAuthorizationUrl();

        response.sendRedirect(url);
    }

    // 2단계: 카카오가 인가 코드를 여기로 보내줌
    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        String accessToken = kakaoAuthService.getAccessToken(code);
        KakaoAuthService.KakaoUserInfo info = kakaoAuthService.getKakaoUserInfo(accessToken);
        String serviceToken = kakaoAuthService.loginOrRegister(info);


        response.sendRedirect("http://localhost:3000/?token=" + serviceToken);
    }
}