package docuTag.domain.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        // client 정보는 property 값을 통해 자동 설정됨
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                "/oauth2/authorization"  // 로그인 시작 baseUri
        );
    }

    // 요청에서 자동으로 provider 판별해서 처리
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/oauth2/authorization")) {
            return null;
        }

        log.info("★ Resolver 진입: {}", request.getRequestURI());
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        log.info("★ Resolver 결과: {}", authorizationRequest);

        return customize(authorizationRequest);
    }

    // registrationId를 직접 지정해서 처리 (ex: "kakao")
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/oauth2/authorization")) {
            return null;
        }

        log.info("★ Resolver 진입 custom : {}", request.getRequestURI());
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        log.info("★ Resolver 결과: custom{}", authorizationRequest);
        return customize(authorizationRequest);
    }

    // 카카오 전용 추가 파라미터 설정
    // 로그인 완료 후 redirect url 설정하고 싶음 여기서 state 필드에 값 설정 후 같이 전송
    // 이후 다시 받아 redirect 시 사용
    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest request) {
        if (request == null) {
            return null;
        }

        // 카카오 전용 추가 파라미터
        Map<String, Object> additionalParams = new HashMap<>(request.getAdditionalParameters());

        // 카카오: 동의 화면 강제 표시 여부
        // 없으면 → 이미 로그인된 경우 자동 로그인
        additionalParams.put("prompt", "login");

        return OAuth2AuthorizationRequest.from(request)
                .additionalParameters(additionalParams)
                .build();
    }
}
