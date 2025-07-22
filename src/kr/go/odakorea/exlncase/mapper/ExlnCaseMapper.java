package kr.go.odakorea.exlncase.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;
/**
 * 우수사례를 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.exlncase.mapper
 * @fileName       : ExlnCaseMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Mapper("exlncase.mapper.ExlnCaseMapper")
public interface ExlnCaseMapper {
	
	/**
	 * 우수사례 지표를 등록한다
	 * @param params
	 */
	void createArtcl(FtMap params);
	
	/**
	 * 우수사례 지표 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listArtcl(Pageable pageable, FtMap params);

	/**
	 * 우수사례 지표 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
	long countListArtcl(FtMap params);
	
	/**
	 * 우수사례 지표를 수정한다
	 * @param params
	 */
	void updateArtcl(FtMap params);
    

	/**
	 * 우수사례 지표를 삭제한다
	 * @param params
	 */
	void deleteArtcl(FtMap params);
	
	/**
	 * 우수사례 항목을 조회한다
	 * @param params
	 */
	Map selectCriteria(Map params);

	/**
	 * 우수사례 항목을 수정한다
	 * @param params
	 */
	void updateCriteria(Map params);

	/**
	 * 우수사례 항목을 등록한다
	 * @param params
	 */
    void createCriteria(Map params);
    
    /**
     * 우수사례 항목을 삭제한다
     * @param params
     */
    void deleteCriteria(Map params);
    
    
    /**
     * 평가항목을 등록한다
     * @param params
     */
    void createEvaluationItems(Map params);
    
    /**
	 * 평가항목을  조회한다
	 * @param params
	 */
	Map selectEvaluationItems(Map params);

	/**
	 * 평가항목을  수정한다
	 * @param params
	 */
	void updateEvaluationItems(Map params);
    
    /**
     * 평가항목을 삭제한다
     * @param params
     */
    void deleteEvaluationItems(Map params);
    
	/**
	 * 사업 우수사례대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
    List<FtMap> listExlnCase(Pageable pageable, FtMap params);
    
	/**
	 * 사업 우수사례대상 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
    long countListExlnCase(FtMap params);
    
    /**
     * 사업 우수사례대상 상세내용을 조회한다 
     * @param params
     * @return
     */
    FtMap getExlnCase(FtMap params);

    /**
     * 우수사례내용을 등록한다
     * @param params
     */
    void createExlnCase(FtMap params);
    
    
	/**
	 * 우수사례내용을 수정한다
	 * @param params
	 */
    void updateExlnCase(FtMap params);

	/**
	 * 우수사례내용을 삭제한다
	 * @param params
	 */
    void deleteExlnCase(FtMap params);
    

	/**
	 * 우수사례 현황 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listExlnCasePrst(Pageable pageable, FtMap params);

	/**
	 * 우수사례 현황 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
	long countListExlnCasePrst(FtMap params);
	
}
