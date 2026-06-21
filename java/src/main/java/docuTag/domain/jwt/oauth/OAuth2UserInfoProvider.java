package docuTag.domain.jwt.oauth;

import java.util.Map;

public interface OAuth2UserInfoProvider {

    /**
     * 지원하는 OAuth Provider 이름
     * ex) google, kakao, naver
     */
    String getProvider();

    /**
     * Provider별 OAuth2UserInfo 생성
     */
    OAuth2UserInfo create(Map<String, Object> attributes);
}
