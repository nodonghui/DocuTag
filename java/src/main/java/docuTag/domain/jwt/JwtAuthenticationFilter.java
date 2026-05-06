package docuTag.domain.jwt;

import docuTag.domain.jwt.service.BlackListService;
import docuTag.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final BlackListService blackListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 쿠키에서 액세스 토큰 추출
        String token = resolveToken(request);



        // 2. 토큰 검증 + Security Context 저장
        if (token != null && jwtUtil.validateToken(token)) {
            try {
                // 리프레시 토큰으로 인증 시도하는 것 방지
                if ("refresh".equals(jwtUtil.getTokenType(token))) {
                    log.warn("액세스 토큰 자리에 리프레시 토큰 사용 시도");
                    filterChain.doFilter(request, response);
                    return;
                }

                if(blackListService.isBlacklisted(token)) {{
                    log.warn("블랙리스트 엑세스 토큰 사용");
                    filterChain.doFilter(request, response);
                    return;
                }}

                // 3. userId로 유저 조회
                Long userId = jwtUtil.getUserId(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUserId(userId);

                // 4. Security Context에 인증 정보 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("JWT 인증 성공 - userId: {}", userId);

            } catch (Exception e) {
                log.error("JWT 인증 처리 중 오류: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    // 쿠키에서 액세스 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
