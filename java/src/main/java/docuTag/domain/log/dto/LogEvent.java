package docuTag.domain.log.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LogEvent {
    private String type;       // "click", "page_view" 등
    private String userId;
    private String page;
    private Map<String, Object> meta;  // 원본 데이터 그대로
    private long timestamp;
}
