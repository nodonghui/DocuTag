package docuTag.domain.jwt;

import docuTag.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SecurityUser implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // 일반 로그인용 생성자
    public SecurityUser(User user) {
        this.user = user;
    }

    // OAuth 로그인용 생성자
    public SecurityUser(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 내 엔티티 꺼내기
    public User getUser() { return user; }
    public Long getUserId() { return user.getUserId(); }

    // ===== UserDetails 구현 =====
    @Override
    public String getUsername() { return String.valueOf(user.getNickname()); }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    // ===== OAuth2User 구현 =====
    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public String getName() { return String.valueOf(user.getNickname()); }
}
