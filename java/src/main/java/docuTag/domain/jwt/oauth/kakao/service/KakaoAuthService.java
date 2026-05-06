package docuTag.domain.jwt.oauth.kakao.service;

import docuTag.global.exception.KakaoApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {
    /*
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
        log.info("getAccessToken() code: {}", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            return (String) response.getBody().get("access_token");

        } catch (HttpClientErrorException e) {
            handleKakaoError(e, "액세스 토큰 요청");
            return null; // 도달 안함
        } catch (HttpServerErrorException e) {
            handleKakaoServerError(e, "액세스 토큰 요청");
            return null;
        }
    }

    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<KakaoUserResponseDto> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    KakaoUserResponseDto.class
            );

            KakaoUserResponseDto dto = response.getBody();
            return new KakaoUserInfo(dto.getId(), dto.getEmail(), dto.getNickname());

        } catch (HttpClientErrorException e) {
            handleKakaoError(e, "사용자 정보 요청");
            return null;
        } catch (HttpServerErrorException e) {
            handleKakaoServerError(e, "사용자 정보 요청");
            return null;
        }
    }

    @Transactional
    public String loginOrRegister(KakaoUserInfo info) {
        User user = findOrCreateUser(info);
        return jwtUtil.createAccessToken(user.getUserId());
    }

    private User findOrCreateUser(KakaoUserInfo info) {
        return userRepository.findByKakaoId(info.kakaoId())
                .orElseGet(() -> createUser(info));
    }

    private User createUser(KakaoUserInfo info) {
        try {
            return userRepository.save(
                    User.builder()
                            .oauthProvider("kakao")
                            .kakaoId(info.kakaoId())
                            .email(info.email())
                            .nickname(info.nickname())
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 인한 중복 저장 시도 → 이미 저장된 유저 반환
            return userRepository.findByKakaoId(info.kakaoId())
                    .orElseThrow(() -> new IllegalStateException(
                            "유저 저장 실패 및 조회 불가. kakaoId: " + info.kakaoId(), e
                    ));
        }
    }

     */

    private void handleKakaoError(HttpClientErrorException e, String context) {
        int httpStatus = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        int kakaoCode = extractKakaoCode(responseBody);

        log.error("[{}] 카카오 API 오류 - HTTP: {}, kakaoCode: {}, body: {}",
                context, httpStatus, kakaoCode, responseBody);

        // 카카오 에러코드별 처리 (여기에 계속 추가)
        switch (kakaoCode) {
            case -401  -> throw new KakaoApiException("유효하지 않은 앱키 또는 액세스 토큰", httpStatus, kakaoCode);
            case -2    -> throw new KakaoApiException("필수 파라미터 누락 또는 타입 오류", httpStatus, kakaoCode);
            case -3    -> throw new KakaoApiException("API 기능 미활성화 또는 설정 누락", httpStatus, kakaoCode);
            case -4    -> throw new KakaoApiException("제재된 계정", httpStatus, kakaoCode);
            case -5    -> throw new KakaoApiException("API 요청 권한 없음", httpStatus, kakaoCode);
            case -10   -> throw new KakaoApiException("요청 횟수 초과 (Rate Limit)", httpStatus, kakaoCode);
            case -101  -> throw new KakaoApiException("카카오계정 연결 필요", httpStatus, kakaoCode);
            case -402  -> throw new KakaoApiException("사용자 동의 필요", httpStatus, kakaoCode);
            case -406  -> throw new KakaoApiException("14세 미만 사용자 접근 불가", httpStatus, kakaoCode);
            case -903  -> throw new KakaoApiException("등록되지 않은 앱키", httpStatus, kakaoCode);
            // 분류 안된 에러 → 로그 남기고 추후 추가
            default    -> throw new KakaoApiException("카카오 API 오류: " + kakaoCode, httpStatus, kakaoCode);
        }
    }

    private void handleKakaoServerError(HttpServerErrorException e, String context) {
        int httpStatus = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        int kakaoCode = extractKakaoCode(responseBody);

        log.error("[{}] 카카오 서버 오류 - HTTP: {}, kakaoCode: {}", context, httpStatus, kakaoCode);

        switch (kakaoCode) {
            case -1    -> throw new KakaoApiException("카카오 서버 내부 오류 (재시도 필요)", httpStatus, kakaoCode);
            case -7    -> throw new KakaoApiException("카카오 서비스 점검 중", httpStatus, kakaoCode);
            case -9798 -> throw new KakaoApiException("카카오 서비스 점검 중", httpStatus, kakaoCode);
            default    -> throw new KakaoApiException("카카오 서버 오류: " + kakaoCode, httpStatus, kakaoCode);
        }
    }

    /** 카카오 응답 body에서 code 추출 */
    private int extractKakaoCode(String responseBody) {
        try {
            Map<String, Object> map = new ObjectMapper().readValue(responseBody, Map.class);
            return (int) map.getOrDefault("code", 0);
        } catch (Exception e) {
            log.warn("카카오 에러 코드 파싱 실패: {}", responseBody);
            return 0;
        }
    }
}
