package docuTag.domain.jwt.oauth.kakao;

import docuTag.domain.jwt.oauth.OAuth2UserInfo;

import java.util.HashMap;
import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.getOrDefault("kakao_account", new HashMap<>());
        return (String) kakaoAccount.getOrDefault("email", "");
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.getOrDefault("kakao_account", new HashMap<>());
        Map<String, Object> profile =
                (Map<String, Object>) kakaoAccount.getOrDefault("profile", new HashMap<>());
        return (String) profile.getOrDefault("nickname", "");
    }

    @Override
    public String getProvider() { return "kakao"; }
}
