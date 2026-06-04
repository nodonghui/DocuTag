package docuTag.global.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@TestConfiguration
public class DataSourceProxyConfig {

    public static final SqlCountLogger SQL_COUNT_LOGGER = new SqlCountLogger();

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        // HikariCP 원본 생성
        DataSource original = properties.initializeDataSourceBuilder().build();

        // 프록시로 감싸서 반환 → JPA가 이 DataSource 사용
        return CountingDataSource.wrap(original, SQL_COUNT_LOGGER);
    }
}
