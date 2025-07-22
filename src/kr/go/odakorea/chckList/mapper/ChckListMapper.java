package kr.go.odakorea.chckList.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;
/**
 * 체크리스트를 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.chckList.mapper
 * @fileName       : ChckListMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Mapper("chckList.mapper.ChckListMapper")
public interface ChckListMapper {
	
	/**
	 * 체크리스트 지표를 등록한다
	 * @param params
	 */
	void createIdct(FtMap params);
	
	/**
	 * 체크리스트 지표 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listIdct(Pageable pageable, FtMap params);

	/**
	 * 체크리스트 지표 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
	long countListIdct(FtMap params);
	
	/**
	 * 체크리스트 지표를 수정한다
	 * @param params
	 */
	void updateIdct(FtMap params);
	
	/**
	 * 체크리스트 지표를 삭제한다
	 * @param params
	 */
	void deleteIdct(FtMap params);
	
	/**
	 * 체크리스트 지표 항목을 조회한다
	 * @param params
	 */
	Map getCriteria(Map params);

	/**
	 * 체크리스트 지표 항목을 수정한다
	 * @param params
	 */
	void updateCriteria(Map params);

	/**
	 * 체크리스트 지표 항목을 등록한다
	 * @param params
	 */
    void createCriteria(Map params);
    
    /**
     * 체크리스트 지표 항목을 삭제한다
     * @param params
     */
    void deleteCriteria(Map params);
    
	/**
	 * 사업 체크리스트대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
    List<FtMap> getListChckList(Pageable pageable, FtMap params);
    
	/**
	 * 사업 체크리스트대상 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
    long countListChckList(FtMap params);
    
    /**
     * 사업 체크리스트대상 상세내용을 조회한다 
     * @param params
     * @return
     */
    FtMap getChckList(FtMap params);

    /**
     * 체크리스트내용을 등록한다
     * @param params
     */
    void createChckList(FtMap params);
    
    
	/**
	 * 체크리스트내용을 수정한다
	 * @param params
	 */
    void updateChckList(FtMap params);

	/**
	 * 체크리스트내용을 삭제한다
	 * @param params
	 */
    void deleteChckList(FtMap params);
    

	/**
	 * 체크리스트 현황 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listChckListPrst(Pageable pageable, FtMap params);

	/**
	 * 체크리스트 현황 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
	long countChckListPrst(FtMap params);
	
}
