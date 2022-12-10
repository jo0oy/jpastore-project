package jpabook.jpastore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class TestDBConfig {

    private final EntityManager em;

    @Bean
    public DatabaseCleanUp databaseCleanUp() {
        return new DatabaseCleanUp(em);
    }
}
