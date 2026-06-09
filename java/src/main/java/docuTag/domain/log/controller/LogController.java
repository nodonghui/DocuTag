package docuTag.domain.log.controller;

import docuTag.domain.log.dto.LogEvent;
import docuTag.domain.log.service.LogProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogProducerService producer;

    @PostMapping("/click")
    public ResponseEntity<Void> receiveLog(@RequestBody LogEvent event) {
        event.setTimestamp(System.currentTimeMillis());
        producer.sendLog(event);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test() {

        return ResponseEntity.ok().build();
    }
}
