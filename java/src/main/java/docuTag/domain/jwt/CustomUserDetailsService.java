package docuTag.domain.jwt;

import docuTag.domain.user.entity.User;
import docuTag.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// jwt 방식으로 로그인 해 작동 안함
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 일반 로그인 유저만 조회 (oauthProvider = "local")
        User user = userRepository.findByEmailAndOauthProvider(email, "local")
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + email));
        return new SecurityUser(user);
    }

    // JWT 필터용
    public UserDetails loadUserByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + userId));
        return new SecurityUser(user);
    }
}
