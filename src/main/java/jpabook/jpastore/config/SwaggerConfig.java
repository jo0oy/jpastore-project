package jpabook.jpastore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "온라인 서점 서비스(Jpastore) API 명세서",
                description = "온라인 서점 서비스(Jpastore)의 API 명세서입니다.",
                version = "v1")
)
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi authGroupOpenApi() {
        String[] path = {"/api/**/auth/**"};

        return GroupedOpenApi.builder()
                .group("인증 API")
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi categoryGroupOpenApi() {
        String[] path = {"/api/**/categories/**", "/api/**/simple-categories/**"};

        return GroupedOpenApi.builder()
                .group("카테고리 API")
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi itemGroupOpenApi() {
        String[] path = {"/api/**/items/**"};

        return GroupedOpenApi.builder()
                .group("상품 API")
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi membershipGroupOpenApi() {
        String[] path = {"/api/**/memberships/**"};

        return GroupedOpenApi.builder()
                .group("멤버십 API")
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi orderGroupOpenApi() {
        String[] path = {"/api/**/orders/**", "/api/**/simple-orders/**"};

        return GroupedOpenApi.builder()
                .group("주문 API")
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi memberGroupOpenApi() {
        String[] path = {"/api/**/members/**"};

        return GroupedOpenApi.builder()
                .group("회원 API")
                .pathsToMatch(path)
                .build();
    }

    @Bean
    public GroupedOpenApi reviewGroupOpenApi() {
        String[] path = {"/api/**/reviews/**"};

        return GroupedOpenApi.builder()
                .group("리뷰 API")
                .pathsToMatch(path)
                .build();
    }
}
