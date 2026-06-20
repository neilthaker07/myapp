package com.demo.myapp.aspect;

import com.demo.myapp.util.AppLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// AOP — Aspect: cross-cutting concern applied transparently via Spring-generated proxy
// BookService never knows this code runs around its methods
@Aspect
@Component
public class PerformanceAspect {

    private final AppLogger logger = AppLogger.getInstance();

    // Pointcut expression breakdown:
    //   execution( *    com.demo.myapp.service.BookService . *   (..) )
    //              ↑    ↑                                    ↑    ↑
    //         any return  exact class                   any method  any args
    @Around("execution(* com.demo.myapp.service.BookService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        logger.info("AOP [ENTER] BookService." + methodName + "() args=" + args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed(); // → calls the REAL BookService method
            long elapsed = System.currentTimeMillis() - start;
            logger.info("AOP [EXIT]  BookService." + methodName + "() took " + elapsed + "ms ✓");
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            logger.warn("AOP [ERROR] BookService." + methodName + "() threw "
                    + ex.getClass().getSimpleName() + " after " + elapsed + "ms");
            throw ex; // rethrow — aspect never swallows exceptions
        }
    }
}
