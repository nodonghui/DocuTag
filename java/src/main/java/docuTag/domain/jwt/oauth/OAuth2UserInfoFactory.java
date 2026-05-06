package docuTag.domain.jwt.oauth;

import docuTag.domain.jwt.oauth.google.GoogleOAuth2UserInfo;
import docuTag.domain.jwt.oauth.kakao.KakaoOAuth2UserInfo;
import docuTag.global.exception.ServiceException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "kakao"  -> new KakaoOAuth2UserInfo(attributes);
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            default -> throw new ServiceException(400, "지원하지 않는 OAuth provider: " + provider);
        };
    }
}
