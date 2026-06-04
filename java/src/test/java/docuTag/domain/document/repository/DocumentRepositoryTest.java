package docuTag.domain.document.repository;

import docuTag.domain.document.dto.DocumentDto;
import docuTag.domain.document.dto.DocumentWithTagsDto;
import docuTag.domain.document.entity.Document;
import docuTag.domain.document.service.DocumentService;
import docuTag.global.config.CountingDataSource;
import docuTag.global.config.DataSourceProxyConfig;
import docuTag.global.config.SqlCountLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Import(DataSourceProxyConfig.class)
@Transactional(readOnly = true)
class DocumentRepositoryTest {

    @Autowired DocumentRepository documentRepository;
    @Autowired
    DocumentService documentService;

    private static final List<String> TAG_NAMES  = List.of("tag_34", "tag_433", "tag_10");
    private static final Long         USER_ID     = 1L;
    private static final Long         LAST_ID     = 5190411L;
    private static final String       TITLE       = "title";
    private static final int          PAGE_SIZE   = 10;

    @BeforeEach
    void setUp() {
        DataSourceProxyConfig.SQL_COUNT_LOGGER.clear();  // 매 테스트 전 초기화
    }

    // db 내부 index 설정 x
    // ===== DISTINCT_JOIN_LAZY =====
    // 소요 시간      : 440ms
    // 총 SQL 실행 수 : 38
    // 419
    //@Test
    @DisplayName("DISTINCT JOIN + LazyLoading N+1 테스트")
    void distinctJoin_lazyLoading() {
        List<String> tagNames = List.of("tag_34", "tag_433", "tag_10");
        Long userId  = 1L;
        Long lastId  = 5190411L;
        String title = "title";
        int size     = 10;

        long start = System.currentTimeMillis();

        // 쿼리 1 — DISTINCT JOIN으로 document 조회
        List<Document> documents = documentRepository.findDistinctByTagNames(
                tagNames, userId, lastId, title, size
        );

        // N+1 발생 구간 — document마다 documentTags, tag 개별 조회
        List<DocumentDto> documentDtos = documents.stream()
                .map(DocumentDto::from)
                .toList();

        long end = System.currentTimeMillis();

        DataSourceProxyConfig.SQL_COUNT_LOGGER.printResult("DISTINCT_JOIN_LAZY", end - start);
        System.out.println("조회된 문서 수: " + documentDtos.size());
        documentDtos.forEach(dto ->
                System.out.println("문서 ID: " + dto.getDocumentId()
                        + " / 제목: " + dto.getTitle()
                        + " / 태그: " + dto.getTags())
        );
    }

    // ─────────────────────────────────────────────
    // 테스트 1. 단일 쿼리 — GROUP BY (GROUP_CONCAT)
    // 소요 시간      : 443ms
    // 총 SQL 실행 수 : 1
    // SQL 실행 시간 : 442
    // split group by data : 1
    // ─────────────────────────────────────────────
    // 443(442+1), 300(285+15), 348(348+0), 498(498+0), 408(408+0),348(348+0)
    // 390ms
    //@Test
    @DisplayName("테스트 1. 단일 쿼리 — GROUP BY (GROUP_CONCAT)")
    void singleQuery_groupBy() {
        long t1 = System.currentTimeMillis();

        List<Object[]> rows = documentRepository.findFilteredWithTagsGrouped(
                USER_ID, LAST_ID, TITLE, TAG_NAMES, PAGE_SIZE
        );

        long t2 = System.currentTimeMillis();
        List<DocumentWithTagsDto> dtos = rows.stream()
                .map(row -> new DocumentWithTagsDto(
                        ((Number)    row[0]).longValue(),
                        ((Number)    row[1]).longValue(),
                        (String)     row[2],
                        (String)     row[3],
                        toLocalDateTime(row[4]),
                        toLocalDateTime(row[5]),
                        (String)     row[6]
                ))
                .collect(Collectors.toList());

        long t3 = System.currentTimeMillis();

        DataSourceProxyConfig.SQL_COUNT_LOGGER.printResult("단일쿼리_GROUP_BY", t3 - t1);
        System.out.println("SQL 실행 시간 : " + (t2-t1));
        System.out.println("split group by data : " + (t3-t2));
        printResult(dtos);
    }

    // ─────────────────────────────────────────────
    // 테스트 2. 단일 쿼리 — NO GROUP BY (LinkedHashMap 그루핑)
    // 소요 시간      : 381ms
    // 총 SQL 실행 수 : 1
    // mapping data : 0ms
    // 381, 401(401+0), 438, 474(474+0),328, 244,
    // 401ms
    // ─────────────────────────────────────────────
    //@Test
    @DisplayName("테스트 2. 단일 쿼리 — NO GROUP BY (LinkedHashMap 그루핑)")
    void singleQuery_noGroupBy() {
        long t1 = System.currentTimeMillis();

        List<Object[]> rows = documentRepository.findFilteredWithTags(
                USER_ID, LAST_ID, TITLE, TAG_NAMES, PAGE_SIZE
        );

        long t2 = System.currentTimeMillis();

        Map<Long, DocumentWithTagsDto> dtoMap = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Long documentId = ((Number) row[0]).longValue();

            dtoMap.computeIfAbsent(documentId, id -> new DocumentWithTagsDto(
                    id,
                    ((Number)    row[1]).longValue(),
                    (String)     row[2],
                    (String)     row[3],
                    toLocalDateTime(row[4]),
                    toLocalDateTime(row[5])
            ));

            dtoMap.get(documentId).addTagName((String) row[6]);
        }
        List<DocumentWithTagsDto> dtos = new ArrayList<>(dtoMap.values());

        long t3 = System.currentTimeMillis();

        DataSourceProxyConfig.SQL_COUNT_LOGGER.printResult("단일쿼리_NO_GROUP_BY", t3 - t1);
        System.out.println("SQL 실행 시간 : " + (t2-t1));
        System.out.println("mapping data : " + (t3-t2));
        printResult(dtos);
    }

    // ─────────────────────────────────────────────
    // 테스트 3. 두 번 쿼리 — ID 추출 후 fetch join
    // 소요 시간      : 323ms
    // 총 SQL 실행 수 : 2
    // 첫번째 SQL 실행 시간 : 150
    // 두번째 SQL 실행 시간 : 170
    // convert data time: 3
    // 350(200+150), 323(150+170), 332, 349, 230(102 + 128), 406(236+170) 450(311+139)
    // ─────────────────────────────────────────────
    //@Test
    @DisplayName("테스트 3. 두 번 쿼리 — ID 추출 후 fetch join")
    void twoQuery_fetchJoin() {
        long t1 = System.currentTimeMillis();

        // 쿼리 1 — 필터링된 ID 추출
        List<Long> ids = documentRepository.findDocumentIdsByTagNamesPaging(
                USER_ID, LAST_ID, TITLE, TAG_NAMES, PAGE_SIZE
        );

        long t2 = System.currentTimeMillis();

        List<DocumentWithTagsDto> dtos = Collections.emptyList();

        long t3 = -1l;
        if (!ids.isEmpty()) {
            // 쿼리 2 — fetch join으로 태그 포함 조회
            List<Document> documents = documentRepository.findAllWithTags(ids);

            t3 = System.currentTimeMillis();

            dtos = documents.stream()
                    .map(doc -> {
                        DocumentWithTagsDto dto = new DocumentWithTagsDto(
                                doc.getDocumentId(),
                                doc.getUser().getUserId(),
                                doc.getTitle(),
                                doc.getContent(),
                                doc.getCreatedAt(),
                                doc.getUpdatedAt()
                        );
                        doc.getDocumentTags().forEach(dt ->
                                dto.addTagName(dt.getTag().getTagName())
                        );
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        long t4 = System.currentTimeMillis();

        DataSourceProxyConfig.SQL_COUNT_LOGGER.printResult("두번쿼리_FETCH_JOIN", t4 - t1);
        System.out.println("첫번째 SQL 실행 시간 : " + (t2-t1));
        System.out.println("두번째 SQL 실행 시간 : " + (t3-t2));
        System.out.println("convert data time: " + (t4-t3));
        printResult(dtos);
    }

    // ─────────────────────────────────────────────
    // 테스트 4. N+1 — 문서 조회 후 태그 개별 조회
    // 소요 시간      : 634ms
    // 총 SQL 실행 수 : 11
    // 첫번째 SQL 실행 시간 : 398
    // n+1 SQL 실행 시간 : 236
    // 조회된 문서 수: 10
    // 325(117 + 208), 439(175+264),659(484+175), 728(478 + 250)
    // ─────────────────────────────────────────────
    //@Test
    @DisplayName("테스트 4. N+1 — 문서 조회 후 태그 개별 조회")
    void nPlusOne() {
        long t1 = System.currentTimeMillis();

        // 쿼리 1 — ID 추출
        List<Long> ids = documentRepository.findDocumentIdsByTagNamesPaging(
                USER_ID, LAST_ID, TITLE, TAG_NAMES, PAGE_SIZE
        );

        long t2 = System.currentTimeMillis();

        // 쿼리 N — 문서별 태그 개별 조회 (N+1 발생)
        List<DocumentWithTagsDto> dtos = ids.stream()
                .map(id -> {
                    Document doc = documentRepository.findWithTags(id) // 매번 쿼리
                            .orElseThrow();

                    DocumentWithTagsDto dto = new DocumentWithTagsDto(
                            doc.getDocumentId(),
                            doc.getUser().getUserId(),
                            doc.getTitle(),
                            doc.getContent(),
                            doc.getCreatedAt(),
                            doc.getUpdatedAt()
                    );
                    doc.getDocumentTags().forEach(dt ->
                            dto.addTagName(dt.getTag().getTagName())
                    );
                    return dto;
                })
                .collect(Collectors.toList());

        long t3= System.currentTimeMillis();

        DataSourceProxyConfig.SQL_COUNT_LOGGER.printResult("N+1", t3 - t1);
        System.out.println("첫번째 SQL 실행 시간 : " + (t2-t1));
        System.out.println("n+1 SQL 실행 시간 : " + (t3-t2));
        printResult(dtos);
    }


    // ─────────────────────────────────────────────
    // 테스트 4. N+1 — 문서 조회 후 태그 개별 조회
    // 소요 시간      : 347ms
    // 총 SQL 실행 수 : 11
    // 첫번째 SQL 실행 시간 : 170
    // 두번째 SQL 실행 시간 : 177
    // 조회된 문서 수: 10
    // 347
    // ─────────────────────────────────────────────
    @Test
    @DisplayName("테스트 5. 두 번 쿼리 — 태그 없음")
    void twoQuery_noTagName() {
        long t1 = System.currentTimeMillis();

        // 쿼리 1 — 필터링된 ID 추출
        List<Long> ids = documentRepository.findDocumentIdsWithPaging(
                USER_ID, TITLE, LAST_ID, PAGE_SIZE
        );

        long t2 = System.currentTimeMillis();

        List<DocumentWithTagsDto> dtos = Collections.emptyList();

        long t3 = -1l;
        if (!ids.isEmpty()) {
            // 쿼리 2 — fetch join으로 태그 포함 조회
            List<Document> documents = documentRepository.findAllWithTags(ids);

            t3 = System.currentTimeMillis();

            dtos = documents.stream()
                    .map(doc -> {
                        DocumentWithTagsDto dto = new DocumentWithTagsDto(
                                doc.getDocumentId(),
                                doc.getUser().getUserId(),
                                doc.getTitle(),
                                doc.getContent(),
                                doc.getCreatedAt(),
                                doc.getUpdatedAt()
                        );
                        doc.getDocumentTags().forEach(dt ->
                                dto.addTagName(dt.getTag().getTagName())
                        );
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        long t4 = System.currentTimeMillis();

        DataSourceProxyConfig.SQL_COUNT_LOGGER.printResult("태그없음", t4 - t1);
        System.out.println("첫번째 SQL 실행 시간 : " + (t2-t1));
        System.out.println("두번째 SQL 실행 시간 : " + (t3-t2));
        System.out.println("convert data time: " + (t4-t3));
        printResult(dtos);
    }

    // ─────────────────────────────────────────────
    // 공통 유틸
    // ─────────────────────────────────────────────
    private void printResult(List<DocumentWithTagsDto> dtos) {
        System.out.println("조회된 문서 수: " + dtos.size());
        dtos.forEach(dto ->
                System.out.println("문서 ID: " + dto.getDocumentId()
                        + " / 제목: " + dto.getTitle()
                        + " / 태그: " + dto.getTags())
        );
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof Timestamp ts)      return ts.toLocalDateTime();
        if (value instanceof LocalDateTime ldt) return ldt;
        return null;
    }
}