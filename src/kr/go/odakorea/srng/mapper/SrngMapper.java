package kr.go.odakorea.srng.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ResultMap;
import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

/**
 * 심사를 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.srng.mapper
 * @fileName       : SrngMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Mapper("srng.mapper.SrngMapper")
public interface SrngMapper {

    /**
	 * 심사 지표를 등록한다
     * @param params
     */
	void createIdct(FtMap params);
    
    /**
     * 심사 지표 목록을 조회한다
     * @param pageable
     * @param params
     * @return
     */
	List<FtMap> listIdct(Pageable pageable, FtMap params);

    
    /**
     * 심사 지표 목록 Count를 조회한다
     * @param params
     * @return
     */
	long countListIdct(FtMap params);
    
	/**
	 * 심사 지표를 수정한다
	 * @param params
	 */
	void updateIdct(FtMap params);
	
	/**
	 * 심사 지표를 삭제한다
	 * @param params
	 */
	void deleteIdct(FtMap params);
	
	/**
	 * 심사 항목을 조회한다
	 * @param params
	 */
	List<Map> selectCriteria(Map params);

	/**
	 * 심사 항목을 수정한다
	 * @param params
	 */
	void updateCriteria(Map params);
	
	/**
	 * 심사 항목 보완 항목을 등록한다
	 * @param params
	 */
	void createComplement(Map params);
	
	/**
	 * 심사 항목 보완 항목을 수정한다
	 * @param params
	 */
	void updateComplement(Map params);

	/**
	 * 심사 항목을 삭제한다
	 * @param params
	 */
	void deleteComplement(Map params);
	
	/**
	 * 심사 항목을 등록한다
	 * @param params
	 */
    void createCriteria(Map params);

    /**
     * 심사 항목을 삭제한다
     * @param params
     */
    void deleteCriteria(Map params);
    

	/**
	 * 사업 심사대상 목록을 조회한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
    List<FtMap> listSrng(Pageable pageable, FtMap params);

	/**
	 * 사업 심사대상 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
    long countListSrng(FtMap params);

	/**
	 * 사업 심사대상 상세내용을 조회한다 
	 * @param params
	 * @return
	 */
    FtMap getSrng(FtMap params);

	/**
	 * 심사를 등록한다
	 * @param params
	 */
    void createSrng(FtMap params);
    
    /**
     * 심사한 보완항목을 등록한다
     * @param params
     */
    void createSrngComplement(Map params);
    
	/**
	 * 심사한 항목을 등록한다
	 * @param params
	 */
	void createSrngCriteria(Map params);
    
	/**
	 * 심사내용을 수정한다
	 * @param params
	 */
    void updateSrng(FtMap params);
    
    /**
     * 심사한 보완항목을 수정한다
     * @param params
     */
    void updateSrngComplement(Map params);
	/**
	 * 심사한 항목을 수정한다
	 * @param params
	 */
	void updateSrngCriteria(Map params);


	/**
	 * 심사내용을 삭제한다
	 * @param params
	 */
    void deleteSrng(FtMap params);
    /**
     * 심사한 보완항목을 삭제한다
     * @param params
     */
    void deleteSrngComplement(Map params);
	/**
	 * 심사한 항목을 삭제한다
	 * @param params
	 */
	void deleteSrngCriteria(Map params);
	
}
