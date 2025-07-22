package kr.go.odakorea.otcm.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.otcm.mapper.OtcmMapper;
import kr.go.odakorea.otcm.mapper.OtcmVO;

/**
 * 성과를 관리하는 Service 클래스
 * @packageName    : kr.go.odakorea.otcm.service
 * @fileName       : OtcmService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Service("otcm.service.OtcmService")
public class OtcmService extends EgovAbstractServiceImpl {

    @Resource(name="otcm.mapper.OtcmMapper")
    private OtcmMapper otcmMapper;

    
    /**
     * 성과 목표 목록을 조회한다
     * @param pageable
     * @param params
     * @return
     * @throws Exception
     */
    
    public Page<FtMap> listOtcmGoal(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = otcmMapper.listOtcmGoal(pageable, params);
    	long count = otcmMapper.countListOtcmGoal(params);
    	//pageable.setTotalCount(count);
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }
    
    /**
     * 성과 목표를 등록한다
     * @param params
     */
    public void createOtcmGoal(FtMap params) {
    	otcmMapper.createOtcmGoal(params);
    }
    
    /**
     * 성과 목표를 수정한다
     * @param params
     */
    public void updateOtcmGoal(FtMap params) {
    	otcmMapper.updateOtcmGoal(params);
    }
    
    /**
     * 성과 목표를 삭제한다
     * @param params
     */
    public void deleteOtcmGoal(FtMap params) {
    	otcmMapper.deleteOtcmGoal(params);
    }
    
    /**
     * 성과 지표 목록을 조회한다
     * @param pageable
     * @param params
     * @return
     * @throws Exception
     */
    
    public Page<FtMap> listPi(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = otcmMapper.listPi(pageable, params);
    	long count = otcmMapper.countListPi(params);
    	//pageable.setTotalCount(count);
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }
	
	/**
	 * 성과 지표를 등록한다
	 * @param params
	 */
    public void createIdct(FtMap params) {
    	otcmMapper.createIdct(params);
    		
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		otcmMapper.createCriteria(criteria);
    	}
    }

	/**
	 * 성과 지표를 수정한다
	 * @param params
	 */
    public void updateIdct(FtMap params) {
    	//항목 수정
        //항목추가
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		if("U".equals(criteria.get("updateType"))) {
    			otcmMapper.updateCriteria( criteria);
    		} else if("I".equals(criteria.get("updateType"))) {
    			otcmMapper.createCriteria(criteria);
    		} else {
    			otcmMapper.deleteCriteria(criteria);
    		}
    	}
    	otcmMapper.updateIdct(params);
        
    }
    
    /**
     * 성과 지표를 삭제한다
     * @param params
     */
    public void deleteIdct(FtMap params) {
		List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	for(Map criteria : criteriaList) {
    		otcmMapper.deleteCriteria(criteria);
    	}
    	otcmMapper.deleteIdct(params);
    }
    
	/**
	 * 사업 성과대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listOtcm(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = otcmMapper.listOtcm(pageable, params);
		long count = otcmMapper.countListOtcm(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}


	/**
	 * 사업 성과대상 상세내용을 조회한다 
	 * @param params
	 * @return
	 */
    public FtMap getOtcm(FtMap params) {
        return otcmMapper.getOtcm(params);
    }

    /**
     * 성과내용을 등록한다
     * @param params
     */
    public void createOtcm(FtMap params) {
        otcmMapper.createOtcm(params);
    }


	/**
	 * 성과내용을 수정한다
	 * @param params
	 */
    public void updateOtcm(FtMap params) {
        otcmMapper.updateOtcm(params);
    }


	/**
	 * 성과내용을 삭제한다
	 * @param params
	 */
    public void deleteOtcm(FtMap params) {
        otcmMapper.deleteOtcm(params);
    }
    

	/**
	 * 성과 현황(current situation)을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listOtcmPrst(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = otcmMapper.listOtcmPrst(pageable, params);
		long count = otcmMapper.countListOtcmPrst(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}

}