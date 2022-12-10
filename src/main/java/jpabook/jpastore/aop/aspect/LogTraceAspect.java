package jpabook.jpastore.aop.aspect;

import jpabook.jpastore.aop.trace.LogTrace;
import jpabook.jpastore.aop.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class LogTraceAspect {

    private final LogTrace trace;

    public LogTraceAspect(LogTrace trace) {
        this.trace = trace;
    }

    @Around("jpabook.jpastore.aop.pointcuts.PointCuts.allMvc()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {

        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();

            status = trace.begin(message);
            // 타겟 호출
            Object result = joinPoint.proceed();
            trace.end(status);

            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
