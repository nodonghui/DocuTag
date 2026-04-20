package docuTag.domain.kakao.service;

import docuTag.domain.kakao.dto.KakaoUserResponseDto;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.repository.UserRepository;
import docuTag.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public record KakaoUserInfo(Long kakaoId, String email, String nickname) {}

    // 1단계: 인가 코드 요청 URL 생성
    public String getAuthorizationUrl() {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;

        log.error("getAuthorizationUrl()");
        log.error("url : " + url);
        return url;
    }

    // 2단계: 인가 코드 → 액세스 토큰
    public String getAccessToken(String code) {
        log.error("getAccessToken()");
        log.error("url : " + code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                new HttpEntity<>(body, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<KakaoUserResponseDto> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                KakaoUserResponseDto.class  // ← DTO로 변경
        );

        KakaoUserResponseDto dto = response.getBody();

        return new KakaoUserInfo(
                dto.getId(),
                dto.getEmail(),
                dto.getNickname()
        );
    }

    // 신규면 가입, 기존이면 로그인
    public String loginOrRegister(KakaoUserInfo info) {
        User user = userRepository.findByKakaoId(info.kakaoId())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .oauthProvider("kakao")
                                .kakaoId(info.kakaoId())
                                .email(info.email())
                                .nickname(info.nickname())
                                .build()
                ));

        return jwtUtil.createToken(user.getUserId());
    }
}
