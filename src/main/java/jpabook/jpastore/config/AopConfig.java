package jpabook.jpastore.config;

import jpabook.jpastore.aop.aspect.LogTraceAspect;
import jpabook.jpastore.aop.trace.LogTrace;
import jpabook.jpastore.aop.trace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }
}
