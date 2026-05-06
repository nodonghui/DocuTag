package docuTag.domain.jwt.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)  // 불필요한 필드 무시
public class KakaoUserResponseDto {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {

        private String email;

        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;

        private Profile profile;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {
            private String nickname;
        }
    }

    // 편의 메서드
    public String getNickname() {
        if (kakaoAccount == null || kakaoAccount.getProfile() == null) {
            return "카카오사용자";  // 닉네임 동의 안 한 경우 기본값
        }
        return kakaoAccount.getProfile().getNickname();
    }

    public String getEmail() {
        if (kakaoAccount == null) return null;
        return kakaoAccount.getEmail();
    }
}
