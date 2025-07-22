package kr.go.odakorea.exlncase.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.exlncase.mapper.ExlnCaseMapper;
/**
 * 
 * 우수사례를 관리하는 Service 클래스
 * @author futech
 *
 */
@Service("exlncase.service.ExlnCaseService")
public class ExlnCaseService extends EgovAbstractServiceImpl {

    @Resource(name="exlncase.mapper.ExlnCaseMapper")
    private ExlnCaseMapper exlnCaseMapper;

	
	/**
	 * 우수사례 지표를 등록한다
	 * @param params
	 */
    public void createArtcl(FtMap params) {
    	exlnCaseMapper.createArtcl(params);
    		
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		exlnCaseMapper.createCriteria(criteria);
    	}
    }

	/**
	 * 우수사례 지표 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listArtcl(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = exlnCaseMapper.listArtcl(pageable, params);
    	long count = exlnCaseMapper.countListArtcl(params);
    	//pageable.setTotalCount(count);
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }
	
    /**
	 * 우수사례 지표를 수정한다
	 * @param params
	 */
    public void updateArtcl(FtMap params) {
    	//항목 수정
        //항목추가
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		if("U".equals(criteria.get("updateType"))) {
    			exlnCaseMapper.updateCriteria( criteria);
    		} else if("I".equals(criteria.get("updateType"))) {
    			exlnCaseMapper.createCriteria(criteria);
    		} else {
    			exlnCaseMapper.deleteCriteria(criteria);
    		}
    	}
    	exlnCaseMapper.updateArtcl(params);
        
    }
    
    /**
     * 우수사례 지표를 삭제한다
     * @param params
     */
    public void deleteArtcl(FtMap params) {
    	exlnCaseMapper.deleteArtcl(params);
    }


	/**
	 * 사업 우수사례대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listExlnCase(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = exlnCaseMapper.listExlnCase(pageable, params);
		long count = exlnCaseMapper.countListExlnCase(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}


	/**
	 * 사업 우수사례대상 상세내용을 조회한다 
	 * @param params
	 * @return
	 */
    public FtMap getExlnCase(FtMap params) {
        return exlnCaseMapper.getExlnCase(params);
    }

    
    /**
     * 우수사례내용을 등록한다
     * 우수사례대상 사업(project subject to ExlnCase)을 등록한다
     * @param params
     */
    public void createExlnCase(FtMap params) {
    	exlnCaseMapper.createExlnCase(params);
    	
         List<Map> evaluationItemsList = (List<Map>) params.get("evaluationItemsList");
         
         /**
          * 평가항목을 등록한다
          * @param params
          */
         for(Map evaluationItems : evaluationItemsList) {
        	 exlnCaseMapper.createEvaluationItems(evaluationItems);
         }
    }

	/**
	 * 우수사례내용을 수정한다
	 * 제출 시 , 취소 시, 우수사례 시,보류 시, 검토의견 입력, 이행점검 등등
	 * @param params
	 */
    public void updateExlnCase(FtMap params) {
    	

        List<Map> evaluationItemsList = (List<Map>) params.get("evaluationItemsList");
        
        
        for(Map evaluationItems : evaluationItemsList) {
    		if("U".equals(evaluationItems.get("updateType"))) {
    			exlnCaseMapper.updateEvaluationItems( evaluationItems);
    		} else if("I".equals(evaluationItems.get("updateType"))) {
    			exlnCaseMapper.createEvaluationItems(evaluationItems);
    		} else {
    			exlnCaseMapper.deleteEvaluationItems(evaluationItems);
    		}
        }
        exlnCaseMapper.updateExlnCase(params);
    }

	/**
	 * 우수사례내용을 삭제한다
	 * @param params
	 */
    public void deleteExlnCase(FtMap params) {
		List<Map> evaluationItemsList = (List<Map>) params.get("evaluationItemsList");
        
        for(Map evaluationItems : evaluationItemsList) {
        	exlnCaseMapper.deleteEvaluationItems(evaluationItems);
        }
        exlnCaseMapper.deleteExlnCase(params);
    }
    

	/**
	 * 우수사례 현황(ExlnCase Prst)을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listExlnCasePrst(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = exlnCaseMapper.listExlnCasePrst(pageable, params);
		long count = exlnCaseMapper.countListExlnCasePrst(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
    
    
    //------------------------------------------------------------------------------------------우수사례 End---------------------------------------------------------------------------------------//

}