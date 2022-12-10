package jpabook.jpastore.aop.pointcuts;

import org.aspectj.lang.annotation.Pointcut;

public class PointCuts {

    @Pointcut("execution(* jpabook.jpastore.domain..*Repository.*(..))")
    public void allRepositories(){}

    @Pointcut("execution(* jpabook.jpastore.application..*Service*.*(..))")
    public void allServices(){}

    @Pointcut("execution(* jpabook.jpastore.web..*Controller*.*(..))")
    public void allControllers(){}

    @Pointcut("allRepositories() || allServices() || allControllers()")
    public void allMvc(){}
}
