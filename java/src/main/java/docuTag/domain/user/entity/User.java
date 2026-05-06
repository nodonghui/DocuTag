package docuTag.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "oauth_provider", nullable = false, length = 20)
    private String oauthProvider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", length = 200)
    private String password;      // 일반 로그인용 추가, OAuth 유저는 null

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
