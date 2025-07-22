package kr.go.odakorea.intgSrch.service;



import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.intgSrch.mapper.IntgSrchMapper;
import kr.go.odakorea.stats.mapper.StatsMapper;



/**
 * 통합검색을 관리하는  Service 클래스
 * @packageName    : kr.go.odakorea.intgSrch.service
 * @fileName       : IntgSrchService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Service("intgSrch.service.IntgSrchService")
public class IntgSrchService  extends EgovAbstractServiceImpl {
	
	 @Resource(name="intgSrch.mapper.IntgSrchMapper")
	 private IntgSrchMapper intgSrchMapper;
	

	 /**
	 * 통합검색 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */

    public Page<FtMap> listIntgSrch(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = intgSrchMapper.listIntgSrch(pageable, params);
    	long count = intgSrchMapper.conuntListIntgSrch(params);
    	
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }
    
    
    /**
   	 * 통합검색 옵션 설정  조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listIntgSrchOpt(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = intgSrchMapper.listIntgSrchOpt(pageable, params);
		long count = intgSrchMapper.conuntListIntgSrchOpt(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
        
	 /**
	 * 통합검색 상세  조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */

	public Page<FtMap> listIntgSrchDtl(Pageable pageable, FtMap params) throws Exception {
	      	
		List<FtMap> list = intgSrchMapper.listIntgSrchDtl(pageable, params);
		long count = intgSrchMapper.conuntListIntgSrchDtl(params);
		      	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
   
    
       
    
}
