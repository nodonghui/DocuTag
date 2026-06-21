package docuTag.domain.jwt.oauth.google;

import docuTag.domain.jwt.oauth.OAuth2UserInfo;
import docuTag.domain.jwt.oauth.OAuth2UserInfoProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("google")
public class GoogleOAuth2UserInfoProvider
        implements OAuth2UserInfoProvider {

    @Override
    public String getProvider() {
        return "";
    }

    @Override
    public OAuth2UserInfo create(
            Map<String, Object> attributes) {

        return new GoogleOAuth2UserInfo(attributes);
    }
}
