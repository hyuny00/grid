package kr.go.odakorea.stats.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

/**
 * 통계를 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.stats.mapper
 * @fileName       : StatsMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Mapper("stats.mapper.StatsMapper")
public interface StatsMapper {

	
	/**
	 * 사업정보 통계선정할 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listBizInfoStatsSlctn(Pageable pageable, FtMap params);

	/**
	 * 사업정보 통계선정할 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListBizInfoStatsSlctn(FtMap params);

	
	/**
	 * 사업정보 통계선정현황 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listStatsSlctnPrst(Pageable pageable, FtMap params);

	/**
	 * 사업정보 통계선정현황 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListStatsSlctnPrst(FtMap params);
	
	
	/**
	 * 통계관리 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listStatsMng(Pageable pageable, FtMap params);

	/**
	 * 통계관리 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListStatsMng(FtMap params);
	
	/**
	 * 제출현황 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listSbmsnPrst(Pageable pageable, FtMap params);

	/**
	 * 제출현황 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListSbmsnPrst(FtMap params);
	
	
	/**
	 * OECD자료관리 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listOecdDataMng(Pageable pageable, FtMap params);

	/**
	 * OECD자료관리 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListOecdDataMng(FtMap params);
	
	
	/**
	 * OECD자료업로드 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listOecdDataUld(Pageable pageable, FtMap params);

	/**
	 * OECD자료업로드 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListOecdDataUld(FtMap params);
	
	
	/**
	 * 데이터검증 자료를 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listDataVrfc(Pageable pageable, FtMap params);

	/**
	 * 데이터검증 자료 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListDataVrfc(FtMap params);
	
	
	
	/**
	 * 데이터검증목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listDataVrfcList(Pageable pageable, FtMap params);

	/**
	 * 데이터검증목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListDataVrfcList(FtMap params);
	
	
	
	/**
	 * OECD 자료현황을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listOecdDataPrst(Pageable pageable, FtMap params);

	/**
	 * OECD 자료현황 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListOecdDataPrst(FtMap params);
	
	
	
	/**
	 * 통계코드 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listStatsCd(Pageable pageable, FtMap params);

	/**
	 *  통계코드 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListStatsCd(FtMap params);
	
	
	/**
	 * 통계코드 매핑 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listStatsCdMpng(Pageable pageable, FtMap params);

	/**
	 * 통계코드 매핑 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListStatsCdMpng(FtMap params);
	
	
	/**
	 * 통계코드 이력을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listStatsCdHstry(Pageable pageable, FtMap params);

	/**
	 * 통계코드 이력 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListStatsCdHstry(FtMap params);
	
	
	
	 /**
   	 * CRS엑셀 업로드
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listCrsExcl(FtMap params);
	
    /**
   	 * 로데이터엑셀 업로드
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listDataExcl(FtMap params);
    
    
	/**
	 * 통계자료를 수정한다
	 * @param params
	 */
    void updateStats(FtMap params);
    
    /**
	 * 통계자료를 등록한다
	 * @param params
	 */
    void insertStats(FtMap params);
    
    /**
	 * 통계자료를 삭제한다
	 * @param params
	 */
    void deleteStats(FtMap params);
    
    
    
    /**
   	 * 사업정보 조회 자료 엑셀 다운로드 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listStatsExcl(FtMap params);


}
