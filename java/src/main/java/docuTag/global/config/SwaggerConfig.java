package docuTag.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // 전역 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("Document API")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme)
                        .addExamples("400Example", new Example()
                                .summary("잘못된 요청")
                                .value("""
                                        {
                                          "status": 400,
                                          "message": "잘못된 요청입니다",
                                          "timestamp": "2026-05-04T07:08:58.667Z"
                                        }
                                        """))
                        .addExamples("401Example", new Example()
                                .summary("인증 실패")
                                .value("""
                                        {
                                          "status": 401,
                                          "message": "인증 실패",
                                          "timestamp": "2026-05-04T07:08:58.667Z"
                                        }
                                        """))
                        .addExamples("403Example", new Example()
                                .summary("권한 없음")
                                .value("""
                                        {
                                          "status": 403,
                                          "message": "접근 권한이 없습니다",
                                          "timestamp": "2026-05-04T07:08:58.667Z"
                                        }
                                        """))
                        .addExamples("404Example", new Example()
                                .summary("리소스 없음")
                                .value("""
                                        {
                                          "status": 404,
                                          "message": "리소스를 찾을 수 없습니다",
                                          "timestamp": "2026-05-04T07:08:58.667Z"
                                        }
                                        """))
                        .addExamples("500Example", new Example()
                                .summary("내부 서버 오류")
                                .value("""
                                        {
                                          "status": 500,
                                          "message": "내부 서버 오류",
                                          "timestamp": "2026-05-04T07:08:58.667Z"
                                        }
                                        """))
                        .addExamples("502Example", new Example()
                                .summary("외부 API 오류")
                                .value("""
                                       {
                                         "status": 502,
                                         "message": "Gemini API 호출에 실패했습니다",
                                         "timestamp": "2026-05-04T07:08:58.667Z"
                                       }
                                       """))
                        .addExamples("409Example", new Example()
                                .summary("중복 이메일")
                                .value("""
                                       {
                                         "status": 409,
                                         "message": "이미 사용중인 이메일입니다",
                                         "timestamp": "2026-05-04T07:08:58.667Z"
                                       }
                                       """))
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("API 문서")
                .description("Spring Boot REST API")
                .version("1.0.0")
                .contact(new Contact()
                        .name("개발팀")
                        .email("dev@example.com"));
    }
}
