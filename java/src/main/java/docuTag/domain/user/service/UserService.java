package docuTag.domain.user.service;

import docuTag.domain.jwt.refreshtoken.service.RefreshTokenService;
import docuTag.domain.jwt.service.JwtService;
import docuTag.domain.user.dto.LoginRequestDto;
import docuTag.domain.user.dto.SignupRequestDto;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.repository.UserRepository;
import docuTag.global.exception.ServiceException;
import docuTag.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public void signup(SignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ServiceException(409,"이미 사용 중인 이메일입니다");
        }

        User user = User.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(passwordEncoder.encode(dto.getPassword())) // 비밀번호 암호화
                .oauthProvider("local")  // 일반 회원가입
                .build();

        userRepository.save(user);

    }

    public void login(LoginRequestDto dto, HttpServletResponse response) {
        // 1. 이메일로 유저 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ServiceException(401,"이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // 3. JWT 발급 후 쿠키에 저장
        long userId = user.getUserId();
        String jwtToken = jwtUtil.createAccessToken(userId);
        String refreshToken = jwtUtil.createRefreshToken(userId);

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
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found: " + id));
    }

}
