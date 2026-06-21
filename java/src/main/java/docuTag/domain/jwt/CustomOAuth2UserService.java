package docuTag.domain.jwt;

import docuTag.domain.jwt.oauth.OAuth2UserInfo;
import docuTag.domain.jwt.oauth.OAuth2UserInfoFactory;
import docuTag.domain.jwt.oauth.OAuth2UserInfoFactory2;
import docuTag.domain.user.entity.User;
import docuTag.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2UserInfoFactory2 oAuth2UserInfoFactory2;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // provider 자동 판별 (kakao / google)
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.of(provider, oauth2User.getAttributes());
        OAuth2UserInfo userInfo2 = oAuth2UserInfoFactory2.create(provider,oauth2User.getAttributes());

        User user = saveOrUpdate(userInfo);

        return new SecurityUser(user, oauth2User.getAttributes());
    }

    private User saveOrUpdate(OAuth2UserInfo userInfo) {
        return userRepository
                .findByOauthProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .map(existing -> userRepository.save(User.builder()
                        .userId(existing.getUserId())
                        .oauthProvider(userInfo.getProvider())
                        .providerId(userInfo.getProviderId())
                        .email(userInfo.getEmail())
                        .nickname(userInfo.getNickname())
                        .build()))
                .orElseGet(() -> userRepository.save(User.builder()
                        .oauthProvider(userInfo.getProvider())
                        .providerId(userInfo.getProviderId())
                        .email(userInfo.getEmail())
                        .nickname(userInfo.getNickname())
                        .build()));
    }
}
