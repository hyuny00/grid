package kr.go.odakorea.srng.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.srng.mapper.SrngMapper;

/**
 * 심사를 관리하는 Service 클래스
 * @packageName    : kr.go.odakorea.srng.service
 * @fileName       : SrngService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Service("srng.service.SrngService")
public class SrngService extends EgovAbstractServiceImpl {

    @Resource(name="srng.mapper.SrngMapper")
    private SrngMapper srngMapper;
    
    /**
     * 심사 지표 목록을 조회한다
     * @param pageable
     * @param params
     * @return
     * @throws Exception
     */
    public Page<FtMap> listIdct(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = srngMapper.listIdct(pageable, params);
    	long count = srngMapper.countListIdct(params);
    	//pageable.setTotalCount(count);
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }

    /**
     * 심사 지표를 등록한다
     * @param params
     */
    public void createIdct(FtMap params) {
    	srngMapper.createIdct(params);
    		
    	
    	
    	// 전략 부합성 보완 complementing strategic fit
    	List<Map> cpList = (List<Map>) params.get("cpList");
    	for(Map cp : cpList) {
    		srngMapper.createComplement(cp);
    	}

    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
        	srngMapper.createCriteria(criteria);
    	}
    }
    
	/**
	 * 심사 지표를 수정한다
	 * @param params
	 */
    public void updateIdct(FtMap params) {
    	
    	/**
    	 * 심사 지표 전략부합성 보완
    	 */
    	// 전략 부합성 보완 complementing strategic fit
    	List<Map> cpList = (List<Map>) params.get("cpList");
    	for(Map cp : cpList) {
    		if("U".equals(cp.get("updateType"))) {
    			srngMapper.updateComplement( cp);
    		} else if("I".equals(cp.get("updateType"))) {
    			srngMapper.createComplement(cp);
    		} else {
    			srngMapper.deleteComplement(cp);
    		}
    	}
    	
    	
    	//항목 수정
        //항목추가
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		if("U".equals(criteria.get("updateType"))) {
    			srngMapper.updateCriteria( criteria);
    		} else if("I".equals(criteria.get("updateType"))) {
    			srngMapper.createCriteria(criteria);
    		} else {
    			srngMapper.deleteCriteria(criteria);
    		}
    	}
    	srngMapper.updateIdct(params);
        
    }
    
    /**
     * 심사 지표를 삭제한다
     * @param params
     */
    public void deleteIdct(FtMap params) {
    	
    	/**
    	 * 심사 지표 전략부합성 보완
    	 */
    	// 전략 부합성 보완 complementing strategic fit
    	List<Map> cpList = (List<Map>) params.get("cpList");
    	for(Map cp : cpList) {
    		srngMapper.deleteComplement(cp);
    	}
    	
        //항목삭제
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		srngMapper.deleteCriteria(criteria);
    	}
    	
    	srngMapper.deleteIdct(params);
    }

	/**
	 * 사업 심사대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listSrng(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = srngMapper.listSrng(pageable, params);
		long count = srngMapper.countListSrng(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}

	/**
	 * 사업 심사대상 상세내용을 조회한다 
	 * @param params
	 * @return
	 */
    public Map getSrng(FtMap params) {
    	
    	Map returnMap = new HashMap<>();
    	List<Map> criteriaList = srngMapper.selectCriteria(params);
    	FtMap srngData = srngMapper.getSrng(params);
    	returnMap.put("criteriaList", criteriaList);
    	returnMap.put("srngData", srngData);
    	
        return returnMap;
    }

	/**
	 * 심사를 등록한다
	 * @param params
	 */
    public void createSrng(FtMap params) {
        
    	// 사업 정보 등록
    	srngMapper.createSrng(params);


    	// 전략부합성 보완 등록
    	List<Map> complementList = (List<Map>) params.get("complementList");
    	for(Map cp : complementList) {
        	srngMapper.createSrngComplement(cp);
    	}
    	
        
    	// 전략부합성 심사항목 등록
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	for(Map criteria : criteriaList) {
        	srngMapper.createSrngCriteria(criteria);
    	}
    }


	/**
	 * 심사내용을 수정한다
	 * @param params
	 */
    public void updateSrng(FtMap params) {

    	List<Map> complementList = (List<Map>) params.get("complementList");
    	
    	for(Map cp : complementList) {
    		if("U".equals(cp.get("updateType"))) {
    			srngMapper.updateSrngComplement(cp);
    		} else if("I".equals(cp.get("updateType"))) {
    			srngMapper.createSrngComplement(cp);
    		} else {
    			srngMapper.deleteSrngComplement(cp);
    		}
    	}
    	
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		if("U".equals(criteria.get("updateType"))) {
    			srngMapper.updateSrngCriteria(criteria);
    		} else if("I".equals(criteria.get("updateType"))) {
    			srngMapper.createSrngCriteria(criteria);
    		} else {
    			srngMapper.deleteSrngCriteria(criteria);
    		}
    	}
    	
    	srngMapper.updateSrng(params);
    }


	/**
	 * 심사내용을 삭제한다
	 * @param params
	 */
    public void deleteSrng(FtMap params) {
    	srngMapper.deleteSrngCriteria(params);
    	srngMapper.deleteSrngComplement(params);
        srngMapper.deleteSrng(params);
    }

}