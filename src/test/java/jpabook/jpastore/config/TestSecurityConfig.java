package jpabook.jpastore.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
public class TestSecurityConfig implements WebSecurityConfigurer<WebSecurity> {

//    @Bean
//    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests().anyRequest().permitAll();
//
//        return http.build();
//    }

    @Override
    public void init(WebSecurity builder) throws Exception {
        builder.ignoring().requestMatchers(
                new AntPathRequestMatcher("/**")
        );
    }

    @Override
    public void configure(WebSecurity builder) throws Exception {

    }
}
