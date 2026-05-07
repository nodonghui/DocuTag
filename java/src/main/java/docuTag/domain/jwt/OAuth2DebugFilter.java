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

        // Step 1: Authorization request
        if (uri.contains("/oauth2/authorization")) {
            log.warn("=== [OAUTH2 STEP 1] Authorization Request ===");
            log.warn("URI: {}", uri);
            log.warn("SessionID: {}", request.getSession().getId());
        }

        // Step 2: Callback request
        if (uri.contains("/login/oauth2/code")) {
            log.warn("=== [OAUTH2 STEP 2] Callback Request ===");
            log.warn("URI: {}", uri);
            log.warn("URL state param: {}", request.getParameter("state"));
            log.warn("URL code param: {}", request.getParameter("code") != null ? "exists" : "missing");
            log.warn("SessionID: {}", request.getSession(false) != null
                    ? request.getSession(false).getId() : "NO SESSION");

            // Check saved authorizationRequest from session
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object savedRequest = session.getAttribute(
                        "org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository.AUTHORIZATION_REQUEST"
                );
                log.warn("Saved AuthorizationRequest in session: {}",
                        savedRequest != null ? savedRequest.toString() : "NOT FOUND ❌");
            } else {
                log.warn("Session does not exist ❌");
            }

            // Print all cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    log.warn("Cookie: {}={}", cookie.getName(),
                            cookie.getName().contains("oauth2") ? cookie.getValue() : "***");
                }
            } else {
                log.warn("No cookies ❌");
            }
        }

        filterChain.doFilter(request, response);
    }
}
