package com.project.bookstudy.common.aop.redisson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.ELParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.data.mapping.model.SpELContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@Order(1)   //시작: DistributeLock → transaction 종료: DistributeLock ← transaction. commit 후 Lock을 반환 해야한다.
@RequiredArgsConstructor
@Slf4j
public class DistributeLockAop {
    private static final String REDISSON_KEY_PREFIX = "RLOCK_";

    private final RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String key = REDISSON_KEY_PREFIX + distributedLock.key();
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());    // (4)
            if (!available) {
                return false;
            }

            log.info("get lock success {}", key);
            return joinPoint.proceed();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error(">>>>>>>>>> 예외 처리", e);
            throw e;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.error("Redisson Lock Already UnLock", e);
            }
        }
    }
}