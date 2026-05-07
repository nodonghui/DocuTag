package docuTag.domain.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OAuth2DebugFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(OAuth2DebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 1단계: authorization 요청
        if (uri.contains("/oauth2/authorization")) {
            log.warn("=== [OAUTH2 1단계] Authorization 요청 ===");
            log.warn("URI: {}", uri);
            log.warn("SessionID: {}", request.getSession().getId());
        }

        // 2단계: 콜백 요청
        if (uri.contains("/login/oauth2/code")) {
            log.warn("=== [OAUTH2 2단계] Callback 요청 ===");
            log.warn("URI: {}", uri);
            log.warn("URL state 파라미터: {}", request.getParameter("state"));
            log.warn("URL code 파라미터: {}", request.getParameter("code") != null ? "있음" : "없음");
            log.warn("SessionID: {}", request.getSession(false) != null
                    ? request.getSession(false).getId() : "세션없음");

            // 세션에서 저장된 authorizationRequest 확인
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object savedRequest = session.getAttribute(
                        "org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository.AUTHORIZATION_REQUEST"
                );
                log.warn("세션에 저장된 AuthorizationRequest: {}",
                        savedRequest != null ? savedRequest.toString() : "없음 ❌");
            } else {
                log.warn("세션 자체가 없음 ❌");
            }

            // 쿠키 전체 출력
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    log.warn("쿠키: {}={}", cookie.getName(),
                            cookie.getName().contains("oauth2") ? cookie.getValue() : "***");
                }
            } else {
                log.warn("쿠키 없음 ❌");
            }
        }

        filterChain.doFilter(request, response);
    }
}
