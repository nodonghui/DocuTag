package docuTag.domain.jwt.oauth.google;

import docuTag.domain.jwt.oauth.OAuth2UserInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub"); // 구글은 sub
    }

    @Override
    public String getEmail() {
        return (String) attributes.getOrDefault("email", "");
    }

    @Override
    public String getNickname() {
        return (String) attributes.getOrDefault("name", "");
    }

    @Override
    public String getProvider() { return "google"; }
}
