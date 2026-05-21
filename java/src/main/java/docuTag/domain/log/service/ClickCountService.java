package docuTag.domain.log.service;

import docuTag.domain.log.dto.LogEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ClickCountService {

    // 날짜별 클릭 횟수 저장 (ex: "2025-05-14" -> 42)
    private final Map<String, AtomicLong> clickCountByDate = new ConcurrentHashMap<>();

    @KafkaListener(topics = "frontend-logs", groupId = "click-counter")
    public void consume(LogEvent logEvent) {
        // click 타입만 집계
        if (!"click".equals(logEvent.getType())) return;

        String date = toDateString(logEvent.getTimestamp());
        clickCountByDate
                .computeIfAbsent(date, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    // 오늘 클릭 횟수 반환
    public long getTodayClickCount() {
        String today = toDateString(System.currentTimeMillis());
        AtomicLong count = clickCountByDate.get(today);
        return count != null ? count.get() : 0;
    }

    private String toDateString(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd")
                .format(new java.util.Date(timestamp));
    }
}