package kr.go.odakorea.otcm.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

/**
 * 성과를 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.otcm.mapper
 * @fileName       : OtcmMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Mapper("otcm.mapper.OtcmMapper")
public interface OtcmMapper {

	
	/**
	 * 성과 목표 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listOtcmGoal(Pageable pageable, FtMap params);
	
	/**
	 * 성과 목표를 등록한다
	 * @param params
	 */
	void createOtcmGoal(FtMap params);
	
	/**
	 * 성과 목표 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
	long countListOtcmGoal(FtMap params);
	
	
	/**
	 * 성과 목표를 수정한다
	 * @param params
	 */
	void updateOtcmGoal(FtMap params);
	
	/**
	 * 성과 목표를 삭제한다
	 * @param params
	 */
	void deleteOtcmGoal(FtMap params);
	
	/**
	 * 성과 지표 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
	List<FtMap> listPi(Pageable pageable, FtMap params);
	
	/**
	 * 성과 지표를 등록한다
	 * @param params
	 */
	void createIdct(FtMap params);

	/**
	 * 성과 지표 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
	long countListPi(FtMap params);


	/**
	 * 성과 지표를 수정한다
	 * @param params
	 */
	void updateIdct(FtMap params);

	/**
	 * 성과 지표를 삭제한다
	 * @param params
	 */
	void deleteIdct(FtMap params);
	
	/**
	 * 성과 항목을 조회한다
	 * @param params
	 */
	Map selectCriteria(Map params);

	/**
	 * 성과 항목을 수정한다
	 * @param params
	 */
	void updateCriteria(Map params);

	/**
	 * 성과 항목을 등록한다
	 * @param params
	 */
    void createCriteria(Map params);

    /**
     * 성과 항목을 삭제한다
     * @param params
     */
    void deleteCriteria(Map params);
	
    
    /**
     * 성과내용을 등록한다
     * @param params
     */
    void createOtcm(FtMap params);

	/**
	 * 사업 성과대상 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */
    List<FtMap> listOtcm(Pageable pageable, FtMap params);

	/**
	 * 사업 성과대상 목록 Count를 조회한다
	 * @param params
	 * @return
	 */
    long countListOtcm(FtMap params);

	/**
	 * 사업 성과대상 상세내용을 조회한다 
	 * @param params
	 * @return
	 */
    FtMap getOtcm(FtMap params);


	/**
	 * 성과내용을 수정한다
	 * @param params
	 */
    void updateOtcm(FtMap params);


	/**
	 * 성과내용을 삭제한다
	 * @param params
	 */
    void deleteOtcm(FtMap params);
    

    /**
     * 성과 현황(current situation)을 조회한다
     * @param pageable
     * @param params
     * @return
     */
	List<FtMap> listOtcmPrst(Pageable pageable, FtMap params);


    /**
     * 성과 현황(current situation) Count를 조회한다
     * @param params
     * @return
     */
	long countListOtcmPrst(FtMap params);
}
