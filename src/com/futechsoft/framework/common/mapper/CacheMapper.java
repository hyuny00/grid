package com.futechsoft.framework.common.mapper;

import java.sql.Timestamp;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.futechsoft.framework.annotation.Mapper;

@Mapper("framework.common.mapper.CacheMapper")
public interface CacheMapper {
	
	 /**
     * 캐시 이름에 따른 마지막 업데이트 시간 조회
     * @param cacheName 캐시 이름
     * @return 마지막 업데이트 시간 (Timestamp)
     */
    @Select("SELECT last_updated FROM cache_update_log WHERE cache_name =  nvl(#{cacheName},'')")
    Timestamp getLastUpdatedByCacheName(@Param("cacheName") String cacheName);
    
    /**
     * 캐시 정보 삭제
     * @param cacheName 캐시 이름
     * @throws Exception 예외 발생 시
     */
    @Delete("DELETE FROM cache_update_log WHERE cache_name = nvl(#{cacheName},'')")
    void deleteCache(@Param("cacheName") String cacheName) throws Exception;
    
    /**
     * 캐시 정보 삽입
     * @param cacheName 캐시 이름
     * @throws Exception 예외 발생 시
     */
    @Insert("INSERT INTO cache_update_log (cache_name, last_updated) VALUES ( nvl(#{cacheName},''), CURRENT_TIMESTAMP )")
    void insertCache(@Param("cacheName") String cacheName) throws Exception;
    
    /**
     * 현재 시간 조회
     * @return 현재 시간 (Timestamp)
     */
    @Select("SELECT CURRENT_TIMESTAMP FROM db_root")
    Timestamp getCurrentTime();

}
