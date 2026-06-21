package docuTag.domain.jwt.oauth.kakao;

import docuTag.domain.jwt.oauth.OAuth2UserInfo;
import docuTag.domain.jwt.oauth.OAuth2UserInfoProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("kakao")
public class KakaoOAuth2UserInfoProvider
        implements OAuth2UserInfoProvider {

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public OAuth2UserInfo create(
            Map<String, Object> attributes) {

        return new KakaoOAuth2UserInfo(attributes);
    }
}
