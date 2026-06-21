package docuTag.domain.jwt.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2UserInfoFactory2 {

    private final Map<String, OAuth2UserInfoProvider> providers;

    public OAuth2UserInfo create(
            String provider,
            Map<String, Object> attributes) {

        return providers.get(provider)
                .create(attributes);
    }
}


