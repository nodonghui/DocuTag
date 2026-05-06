package docuTag.domain.jwt.oauth;

public interface OAuth2UserInfo {
    String getProviderId();   // 카카오: kakaoId, 구글: sub
    String getEmail();
    String getNickname();
    String getProvider();
}
