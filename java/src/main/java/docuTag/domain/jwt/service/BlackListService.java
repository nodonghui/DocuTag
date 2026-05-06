package docuTag.domain.jwt.service;

import docuTag.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlackListService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void addBlacklist(String accessToken) {
        long remainSeconds = jwtUtil.getExpiresAt(accessToken)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - System.currentTimeMillis();

        if (remainSeconds > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "1",                                      // 값은 의미없음
                    remainSeconds,
                    TimeUnit.MILLISECONDS
            );
        }
        log.info("blocked access token : " + accessToken);
        // 저장 후 전체 key-value 조회
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            keys.forEach(key -> {
                Object value = redisTemplate.opsForValue().get(key);
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                log.info("KEY: {} | VALUE: {} | TTL: {}s", key, value, ttl);
            });
        }
    }

    public boolean isBlacklisted(String accessToken) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken)
        );
    }
}
