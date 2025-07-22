package com.futechsoft.framework.aop;


import java.lang.reflect.Method;
import java.sql.Timestamp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.futechsoft.framework.annotation.CacheAccess;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.common.service.CacheUpdateService;
import com.futechsoft.framework.util.FtMap;

@Aspect
@Component
public class CacheValidationAop {
    private static final Logger logger = LoggerFactory.getLogger(CacheValidationAop.class);
    private static final String DEFAULT_CACHE_NAME = "defaultCache";
    private static final String CACHE_CREATION_TIME_SUFFIX = "_created";
   
    @Autowired
    private  CacheManager cacheManager;

    @Autowired
	private  CacheUpdateService cacheUpdateService; 
    

    @Around("@annotation(com.futechsoft.framework.annotation.CacheAccess)")
    public Object validateCache(ProceedingJoinPoint joinPoint) throws Throwable {
    	
    	
        Object[] args = joinPoint.getArgs();
        Pageable pageable = null;
        FtMap params = null;

        for (Object arg : args) {
            if (arg instanceof Pageable) {
                pageable = (Pageable) arg;
            } else if (arg instanceof FtMap) {
                params = (FtMap) arg;
            }
        }

        return validateCache(joinPoint, pageable, params);
    }

    

    public Object validateCache(ProceedingJoinPoint joinPoint, Pageable pageable, FtMap params) throws Throwable {
    	
    	
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheAccess cacheAccess = method.getAnnotation(CacheAccess.class);

        String cacheName = StringUtils.hasText(cacheAccess.value()) ? cacheAccess.value() : DEFAULT_CACHE_NAME;
        
        String key = generateCacheKey(pageable, params);
        
        logger.debug("Cache validation: cacheName={}, key={}", cacheName, key);

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            logger.warn("Cache not found: {}", cacheName);
            return joinPoint.proceed();
        }

        // 캐시에서 데이터 및 생성 시간 가져오기
        Cache.ValueWrapper cachedData = cache.get(key);
        Long cacheCreationTime = cache.get(key + CACHE_CREATION_TIME_SUFFIX, Long.class);
        
        // 캐시 데이터가 있고 유효한지 확인
        if (cachedData != null && cacheCreationTime != null) {
            // DB에서 마지막 업데이트 시간 가져오기
            Timestamp lastUpdated = getLastUpdatedTimestamp(cacheName);
            
            if (lastUpdated != null) {
                //long lastUpdatedTimeSeconds = lastUpdated.getTime() / 1000;
                //long cacheCreatedTimeSeconds = cacheCreationTime / 1000;
                long lastUpdatedTimeSeconds = lastUpdated.getTime();
                long cacheCreatedTimeSeconds = cacheCreationTime;
                
                // 캐시가 유효하면 캐시에서 데이터를 반환
                if (cacheCreatedTimeSeconds > lastUpdatedTimeSeconds) {
                    logger.debug("Cache hit and valid: {} (cacheTime={}, lastUpdateTime={})", 
                            key, cacheCreatedTimeSeconds, lastUpdatedTimeSeconds);
                    return cachedData.get();
                } else {
                    logger.debug("Cache invalid: {} (cacheTime={}, lastUpdateTime={})", 
                            key, cacheCreatedTimeSeconds, lastUpdatedTimeSeconds);
                }
            } else {
                logger.debug("No last update timestamp found for cache: {}", cacheName);
            }
        } else {
            logger.debug("Cache miss: {}", key);
            cacheUpdateService.updateCacheTimestamp(cacheName);
        }

        // 캐시가 없거나 유효하지 않으면 DB에서 조회
        logger.debug("Fetching fresh data from source for: {}", key);
        Object freshData = joinPoint.proceed();

        // 캐시와 DB의 현재 시간 함께 저장
        saveCacheWithTimestamp(cache, key, freshData);
        
        //System.out.println("현재 캐시 크기: " + caffeineCache.estimatedSize());
        
        /*
    	for (String cacheTmpName : cacheManager.getCacheNames()) {
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>) 
                    ((CaffeineCache) cacheManager.getCache(cacheTmpName)).getNativeCache();
            System.out.println("캐시 '" + cacheName + "' 의 현재 크기: " + nativeCache.estimatedSize());
        }
         */

        return freshData;
    }

    /**
     * 페이징 정보와 파라미터를 사용하여 캐시 키를 생성
     */
    
    private String generateCacheKey(Pageable pageable, FtMap params) {
        StringBuilder keyBuilder = new StringBuilder();
        if(pageable!=null) {
            keyBuilder.append(pageable.getPageNo())
            .append('_')
            .append(pageable.getPageSize())
            .append('_');
        }
        
        // FtMap에서 값이 있는 항목만 포함하고 "csrf" 키는 제외
        if (params != null) {
            params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().toString().isEmpty())  // 값이 null이 아니고 빈 문자열이 아닌 항목만 포함
                .filter(entry -> !"csrf".equals(entry.getKey()))  // "csrf" 키를 제외
                .filter(entry -> !"listPageSize".equals(entry.getKey()))  // "listPageSize" 키를 제외
                .filter(entry -> !"pageNo".equals(entry.getKey()))  // "pageNo" 키를 제외
                .filter(entry -> !"pageSize".equals(entry.getKey()))  // "pageSize" 키를 제외
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(entry -> {
                    keyBuilder.append(entry.getKey())
                            .append('=')
                            .append(entry.getValue())
                            .append(',');
                });
        }   
        
        return keyBuilder.toString();
    }
    
    
    /*
    private String generateCacheKey(Pageable pageable, FtMap params) {
        StringBuilder keyBuilder = new StringBuilder();
        
        if(pageable!=null) {
            keyBuilder.append(pageable.getPageNo())
            .append('_')
            .append(pageable.getPageSize())
            .append('_');
        }
    
        
        // FtMap에서 "sch"로 시작하는 키와 "menuSeq", "topMenuSeq" 키만 포함
        if (params != null) {
            params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().toString().isEmpty())  // 값이 null이 아니고 빈 문자열이 아닌 항목만 포함
                .filter(entry -> {
                    String key = entry.getKey();
                    return key.startsWith("sch") || "menuSeq".equals(key) || "topMenuSeq".equals(key);  // "sch"로 시작하거나 "menuSeq", "topMenuSeq"인 키만 포함
                })
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(entry -> {
                    keyBuilder.append(entry.getKey())
                            .append('=')
                            .append(entry.getValue())
                            .append(',');
                });
        }   
        
        return keyBuilder.toString();
    }*/

    /**
     * 캐시와 DB의 현재 시간을 함께 저장
     */
    private void saveCacheWithTimestamp(Cache cache, String key, Object data) {
        try {
            // DB에서 현재 시간을 밀리초로 가져옴
            Timestamp currentDbTime = getCurrentDatabaseTime();
            if (currentDbTime == null) {
                logger.error("Failed to get current DB time for cache: {}", key);
                return;
            }
            
            long currentTimeMillis = currentDbTime.getTime();
            
            // 캐시에 데이터와 시간을 저장
            cache.put(key, data);
            cache.put(key + CACHE_CREATION_TIME_SUFFIX, currentTimeMillis);
            
            logger.debug("Cache saved: {}, DB timestamp: {}", key, currentTimeMillis);
        } catch (Exception e) {
            logger.error("Error saving cache with timestamp: " + key, e);
        }
    }

    /**
     * DB에서 현재 시간 가져오기
     */
    private Timestamp getCurrentDatabaseTime() {
        try {
            return cacheUpdateService.getCurrentTime();
        } catch (Exception e) {
            logger.error("Failed to get current database time", e);
            return null;
        }
    }

    /**
     * 특정 캐시의 마지막 업데이트 시간 가져오기
     */
    private Timestamp getLastUpdatedTimestamp(String cacheName) {
        try {
            return cacheUpdateService.getLastUpdatedByCacheName(cacheName);
        } catch (Exception e) {
            logger.debug("Failed to get last updated timestamp for cache: " + cacheName, e);
            return null; 
        }
    }
    
    
    
    @Pointcut("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public void cacheEvictMethods() {}

    @Around("cacheEvictMethods()")
    @Transactional  
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
        
        String cacheName = cacheEvict.value().length > 0 ? cacheEvict.value()[0] : DEFAULT_CACHE_NAME;
        
        Object result = joinPoint.proceed();

        cacheUpdateService.updateCacheTimestamp(cacheName);

        return result;
    }
}
