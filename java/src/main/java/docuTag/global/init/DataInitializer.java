package docuTag.global.init;

import docuTag.domain.document.entity.Document;
import docuTag.domain.document.repository.DocumentRepository;
import docuTag.domain.tag.entity.Tag;
import docuTag.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final TagRepository tagRepository;
    private final DocumentRepository documentRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (documentRepository.count() > 0) {
            log.info("[DataInitializer] 이미 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("[DataInitializer] 테스트 데이터 삽입 시작...");

        List<Tag> tags = tagRepository.saveAll(List.of(
                Tag.builder().tagName("Java").build(),
                Tag.builder().tagName("Spring").build(),
                Tag.builder().tagName("JPA").build(),
                Tag.builder().tagName("Docker").build(),
                Tag.builder().tagName("Backend").build(),
                Tag.builder().tagName("Security").build(),
                Tag.builder().tagName("Redis").build(),
                Tag.builder().tagName("MySQL").build(),
                Tag.builder().tagName("AWS").build(),
                Tag.builder().tagName("CI/CD").build()
        ));

        Tag tagJava     = tags.get(0);
        Tag tagSpring   = tags.get(1);
        Tag tagJPA      = tags.get(2);
        Tag tagDocker   = tags.get(3);
        Tag tagBackend  = tags.get(4);
        Tag tagSecurity = tags.get(5);
        Tag tagRedis    = tags.get(6);
        Tag tagMySQL    = tags.get(7);
        Tag tagAWS      = tags.get(8);
        Tag tagCICD     = tags.get(9);

        // 3. 문서 30개 생성 및 태그 연결
        record DocSeed(String title, String content, Tag[] tags) {}

        List<DocSeed> seeds = List.of(
                new DocSeed("Spring Boot 시작하기",         "Spring Boot로 빠르게 프로젝트를 구성하는 방법을 소개합니다.",          new Tag[]{tagSpring, tagJava, tagBackend}),
                new DocSeed("JPA 연관관계 매핑",             "OneToMany, ManyToOne 등 JPA 연관관계 매핑 방법을 정리합니다.",        new Tag[]{tagJPA, tagJava}),
                new DocSeed("Docker로 Spring 배포하기",      "Dockerfile 작성부터 컨테이너 실행까지 단계별로 알아봅니다.",           new Tag[]{tagDocker, tagSpring}),
                new DocSeed("MySQL 인덱스 최적화",           "쿼리 성능을 높이기 위한 인덱스 설계 전략을 설명합니다.",               new Tag[]{tagMySQL, tagBackend}),
                new DocSeed("Spring Security 기본 설정",    "JWT 기반 인증과 Spring Security 필터 체인을 구성합니다.",             new Tag[]{tagSecurity, tagSpring}),
                new DocSeed("Redis 캐시 적용하기",           "Spring Cache와 Redis를 연동하여 조회 성능을 개선합니다.",             new Tag[]{tagRedis, tagSpring}),
                new DocSeed("AWS EC2 배포 가이드",           "EC2 인스턴스 생성부터 애플리케이션 배포까지 정리합니다.",              new Tag[]{tagAWS, tagDocker}),
                new DocSeed("GitHub Actions CI/CD",        "GitHub Actions로 테스트 및 배포 자동화 파이프라인을 구성합니다.",       new Tag[]{tagCICD, tagDocker}),
                new DocSeed("JPA N+1 문제 해결",             "Fetch Join과 EntityGraph를 활용한 N+1 문제 해결법을 다룹니다.",       new Tag[]{tagJPA, tagSpring}),
                new DocSeed("Spring AOP 개념 정리",          "관점 지향 프로그래밍과 Spring AOP의 동작 원리를 설명합니다.",          new Tag[]{tagSpring, tagJava}),
                new DocSeed("Java Stream API 활용",         "Stream API를 이용한 컬렉션 처리 패턴을 예제와 함께 소개합니다.",        new Tag[]{tagJava}),
                new DocSeed("Docker Compose 멀티 컨테이너",  "docker-compose로 Spring + MySQL + Redis 환경을 구성합니다.",         new Tag[]{tagDocker, tagMySQL, tagRedis}),
                new DocSeed("Spring Batch 기초",            "대용량 데이터 처리를 위한 Spring Batch Job 구성 방법입니다.",           new Tag[]{tagSpring, tagJava}),
                new DocSeed("MySQL 트랜잭션과 격리 수준",     "트랜잭션 격리 수준별 동작 차이와 실무 적용 기준을 정리합니다.",         new Tag[]{tagMySQL, tagBackend}),
                new DocSeed("AWS S3 파일 업로드",            "Spring에서 AWS S3에 파일을 업로드하는 방법을 구현합니다.",             new Tag[]{tagAWS, tagSpring}),
                new DocSeed("Redis 분산 락 구현",            "Redisson을 이용한 분산 환경에서의 동시성 제어 방법입니다.",            new Tag[]{tagRedis, tagBackend}),
                new DocSeed("Spring REST Docs 작성",        "테스트 코드 기반으로 API 문서를 자동 생성하는 방법을 설명합니다.",       new Tag[]{tagSpring}),
                new DocSeed("Java 제네릭 완전 정복",          "제네릭 타입 파라미터, 와일드카드, 바운디드 타입을 정리합니다.",         new Tag[]{tagJava}),
                new DocSeed("JPA QueryDSL 적용",            "타입 안전한 동적 쿼리 작성을 위한 QueryDSL 설정과 예제입니다.",         new Tag[]{tagJPA, tagSpring}),
                new DocSeed("CI/CD Blue-Green 배포",        "무중단 배포를 위한 Blue-Green 전략을 GitHub Actions로 구현합니다.",    new Tag[]{tagCICD, tagAWS}),
                new DocSeed("Spring 예외 처리 전략",         "@ControllerAdvice를 활용한 전역 예외 처리 방법을 소개합니다.",         new Tag[]{tagSpring, tagBackend}),
                new DocSeed("MySQL 실행 계획 분석",          "EXPLAIN을 이용해 쿼리 실행 계획을 읽고 최적화하는 방법입니다.",         new Tag[]{tagMySQL}),
                new DocSeed("Java 동시성 프로그래밍",         "synchronized, ReentrantLock, ConcurrentHashMap 활용법을 다룹니다.",  new Tag[]{tagJava, tagBackend}),
                new DocSeed("Spring WebFlux 입문",          "리액티브 프로그래밍과 WebFlux의 기본 개념을 소개합니다.",              new Tag[]{tagSpring, tagJava}),
                new DocSeed("AWS RDS 운영 가이드",           "RDS 파라미터 그룹, 스냅샷, 읽기 복제본 설정 방법을 정리합니다.",        new Tag[]{tagAWS, tagMySQL}),
                new DocSeed("Docker 이미지 최적화",          "멀티 스테이지 빌드로 프로덕션 이미지 크기를 줄이는 방법입니다.",         new Tag[]{tagDocker}),
                new DocSeed("Spring 이벤트 드리븐 설계",      "ApplicationEvent와 @EventListener를 활용한 느슨한 결합 설계입니다.", new Tag[]{tagSpring, tagBackend}),
                new DocSeed("JPA 벌크 연산 주의사항",         "벌크 업데이트/삭제 시 영속성 컨텍스트 동기화 문제를 설명합니다.",       new Tag[]{tagJPA}),
                new DocSeed("Redis Pub/Sub 메시징",          "Redis Pub/Sub을 이용한 실시간 이벤트 처리 구조를 구현합니다.",         new Tag[]{tagRedis, tagBackend}),
                new DocSeed("태그 없는 문서 예시",            "태그가 없는 문서도 정상적으로 저장됩니다.",                           new Tag[]{})
        );

        for (DocSeed seed : seeds) {
            Document doc = Document.builder()
                    .user(null)
                    .title(seed.title())
                    .content(seed.content())
                    .build();
            for (Tag tag : seed.tags()) {
                doc.addTag(tag);
            }
            documentRepository.save(doc);
        }

        log.info("[DataInitializer] 테스트 데이터 삽입 완료! 문서 {}건 생성됨", documentRepository.count());
    }
}
