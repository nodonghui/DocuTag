package docuTag.domain.log.controller;

import docuTag.domain.log.service.ClickCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ClickCountService clickCountService;


    @GetMapping("/clicks/today")
    public ResponseEntity<Map<String, Long>> getTodayClickCount() {
        return ResponseEntity.ok(Map.of(
                "count", clickCountService.getTodayClickCount()
        ));
    }
}
