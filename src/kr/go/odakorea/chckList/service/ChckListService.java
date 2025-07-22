package kr.go.odakorea.chckList.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.chckList.mapper.ChckListMapper;
/**
 * 체크리스트를 관리하는 Service 클래스
 * @packageName    : kr.go.odakorea.chckList.service
 * @fileName       : ChckListService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Service("chckList.service.ChckListService")
public class ChckListService extends EgovAbstractServiceImpl {

    @Resource(name="chckList.mapper.ChckListMapper")
    private ChckListMapper chckListMapper;

	
	/**
	 * 체크리스트 지표를 등록한다
	 * @param params
	 */
    public void createIdct(FtMap params) {
    	chckListMapper.createIdct(params);
    		
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		chckListMapper.createCriteria(criteria);
    	}
    }

	/**
	 * 체크리스트 지표 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listIdct(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = chckListMapper.listIdct(pageable, params);
    	long count = chckListMapper.countListIdct(params);
    	//pageable.setTotalCount(count);
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }
	
    /**
	 * 체크리스트 지표를 수정한다
	 * @param params
	 */
    public void updateIdct(FtMap params) {
    	//항목 수정
        //항목추가
    	List<Map> criteriaList = (List<Map>) params.get("criteriaList");
    	
    	for(Map criteria : criteriaList) {
    		if("U".equals(criteria.get("updateType"))) {
    			chckListMapper.updateCriteria( criteria);
    		} else if("I".equals(criteria.get("updateType"))) {
    			chckListMapper.createCriteria(criteria);
    		} else {
    			chckListMapper.deleteCriteria(criteria);
    		}
    	}
    	chckListMapper.updateIdct(params);
        
    }
    
    /**
     * 체크리스트 지표를 삭제한다
     * @param params
     */
    public void deleteIdct(FtMap params) {
    	chckListMapper.deleteIdct(params);
    }


	/**
	 * 사업 체크리스트대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> getListChckList(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = chckListMapper.getListChckList(pageable, params);
		long count = chckListMapper.countListChckList(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}


	/**
	 * 사업 체크리스트대상 상세내용을 조회한다 
	 * @param params
	 * @return
	 */
    public FtMap getChckList(FtMap params) {
        return chckListMapper.getChckList(params);
    }

    
    /**
     * 체크리스트내용을 등록한다
     * 체크리스트대상 사업(project subject to ChckList)을 등록한다
     * @param params
     */
    public void createChckList(FtMap params) {
    	chckListMapper.createChckList(params);
    	
    	 List<Map> projectSubjectList = (List<Map>) params.get("projectSubjectList");
         List<Map> apsList = (List<Map>) params.get("apsList");
         List<Map> indicatorPlanningList = (List<Map>) params.get("indicatorPlanningList");
         
    }

	/**
	 * 체크리스트내용을 수정한다
	 * 제출 시 , 취소 시, 체크리스트 시,보류 시, 검토의견 입력, 이행점검 등등
	 * @param params
	 */
    public void updateChckList(FtMap params) {
        chckListMapper.updateChckList(params);
    }

	/**
	 * 체크리스트내용을 삭제한다
	 * @param params
	 */
    public void deleteChckList(FtMap params) {
        chckListMapper.deleteChckList(params);
    }
    

	/**
	 * 체크리스트 현황(ChckList Prst)을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public Page<FtMap> listChckListPrst(Pageable pageable, FtMap params) throws Exception {

		List<FtMap> list = chckListMapper.listChckListPrst(pageable, params);
		long count = chckListMapper.countChckListPrst(params);
		//pageable.setTotalCount(count);
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
    
    
    //------------------------------------------------------------------------------------------체크리스트 End---------------------------------------------------------------------------------------//

}