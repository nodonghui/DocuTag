package docuTag.domain.jwt.oauth.kakao.controller;

import docuTag.domain.jwt.oauth.kakao.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
//@RequestMapping("api/auth/kakao")
// 해당 로직 사용시 url 매핑 /api/auth/kakao 로 맞춰야함
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Value("${frontend.base-url}")  // ← 추가
    private String frontendBaseUrl;



    /*
    // 1단계: 카카오 로그인 페이지로 리다이렉트
    // 해당 과정은 customAuthorization 으로 진행
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        try {
            String url = kakaoAuthService.getAuthorizationUrl();
            response.sendRedirect(url);
        } catch (Exception e) {
            // 에러 발생 시 프론트 로그인 페이지로 에러코드와 함께 리다이렉트
            response.sendRedirect(frontendBaseUrl + "/error?error=" + e.getMessage());
        }
    }

     */

    /*
    // 2단계: 카카오가 인가 코드를 여기로 보내줌
    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        try {
            String accessToken = kakaoAuthService.getAccessToken(code);
            KakaoAuthService.KakaoUserInfo info = kakaoAuthService.getKakaoUserInfo(accessToken);
            String serviceToken = kakaoAuthService.loginOrRegister(info);
            response.sendRedirect(frontendBaseUrl + "/?token=" + serviceToken);
        } catch (KakaoApiException e) {
            log.error("카카오 콜백 처리 실패 - kakaoCode: {}", e.getKakaoCode());
            response.sendRedirect(frontendBaseUrl + "/error?error=" + e.getKakaoCode());
        } catch (Exception e) {
            log.error("카카오 콜백 처리 실패: {}", e.getMessage());
            response.sendRedirect(frontendBaseUrl + "/error?error=UNKNOWN");
        }
    }

     */



}