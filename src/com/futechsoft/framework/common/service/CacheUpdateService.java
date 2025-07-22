package com.futechsoft.framework.common.service;

import java.sql.Timestamp;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.mapper.CacheMapper;

@Service
public class CacheUpdateService {
  
    
    @Resource(name = "framework.common.mapper.CacheMapper")
	private CacheMapper cacheMapper;
    
    /**
     * 특정 캐시의 최신 업데이트 시간 기록
     * @throws Exception 
     */
    public void updateCacheTimestamp(String cacheName) throws Exception {
    	if (cacheName != null) {
    		cacheMapper.deleteCache(cacheName);
        	cacheMapper.insertCache(cacheName);
    	}
    }
    
    public Timestamp getLastUpdatedByCacheName(String cacheName) throws Exception {
    	return cacheMapper.getLastUpdatedByCacheName(cacheName);
    }
    
    public Timestamp getCurrentTime() throws Exception {
    	return cacheMapper.getCurrentTime();
    }
}

