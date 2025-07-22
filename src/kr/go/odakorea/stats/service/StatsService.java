package kr.go.odakorea.stats.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.stats.mapper.StatsMapper;


/**
 * 통계를 관리하는 Service 클래스
 * @packageName    : kr.go.odakorea.stats.service
 * @fileName       : StatsService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Service("stats.service.StatsService")
public class StatsService extends EgovAbstractServiceImpl {

	 @Resource(name="stats.mapper.StatsMapper")
	 private StatsMapper statsMapper;

	 
	 /**
	 * 사업정보 통계선정할 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */

    public Page<FtMap> listBizInfoStatsSlctn(Pageable pageable, FtMap params) throws Exception {
    	
    	List<FtMap> list = statsMapper.listBizInfoStatsSlctn(pageable, params);
    	long count = statsMapper.conuntListBizInfoStatsSlctn(params);
    	
    	Page<FtMap> page = new Page<FtMap>(pageable, list, count);
    	return page;
    }
	
    
    /**
   	 * 사업정보 통계선정현황 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listStatsSlctnPrst(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listStatsSlctnPrst(pageable, params);
		long count = statsMapper.conuntListStatsSlctnPrst(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
    
	 /**
   	 * 통계관리 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listStatsMng(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listStatsMng(pageable, params);
		long count = statsMapper.conuntListStatsMng(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	 /**
   	 * 제출현황 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listSbmsnPrst(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listSbmsnPrst(pageable, params);
		long count = statsMapper.conuntListSbmsnPrst(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	 /**
   	 * OECD자료관리 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listOecdDataMng(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listOecdDataMng(pageable, params);
		long count = statsMapper.conuntListOecdDataMng(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	 /**
   	 * OECD자료업로드 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listOecdDataUld(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listOecdDataUld(pageable, params);
		long count = statsMapper.conuntListOecdDataUld(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	/**
   	 * 데이터검증 자료를 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listDataVrfc(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listDataVrfc(pageable, params);
		long count = statsMapper.conuntListDataVrfc(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	
	/**
   	 * 데이터검증목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listDataVrfcList(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listDataVrfcList(pageable, params);
		long count = statsMapper.conuntListDataVrfcList(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	/**
   	 * OECD 자료현황을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listOecdDataPrst(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listOecdDataPrst(pageable, params);
		long count = statsMapper.conuntListOecdDataPrst(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	/**
   	 * 통계코드 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listStatsCd(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listStatsCd(pageable, params);
		long count = statsMapper.conuntListStatsCd(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	
	/**
   	 * 통계코드 매핑 목록을 조회한다
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listStatsCdMpng(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listStatsCdMpng(pageable, params);
		long count = statsMapper.conuntListStatsCdMpng(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	/**
   	 * CRS엑셀 업로드
   	 * @param pageable
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	public Page<FtMap> listStatsCdHstry(Pageable pageable, FtMap params) throws Exception {
	   	
		List<FtMap> list = statsMapper.listStatsCdHstry(pageable, params);
		long count = statsMapper.conuntListStatsCdHstry(params);
		   	
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	
	/**
   	 * CRS엑셀 업로드
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public List listCrsExcl( FtMap params) throws Exception {
	          	
		 List list = statsMapper.listCrsExcl(params);       	
		 return list;
	 }
	
	 /**
   	 * 로데이터 엑셀 업로드
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public List listDataExcl( FtMap params) throws Exception {
	          	
		 List list = statsMapper.listDataExcl(params);       	
		 return list;
	 }
	 
	 
    
    /**
	 *통계자료를 수정 한다
	 * @param params
	 */
    public void updateStats(FtMap params) {
    	statsMapper.updateStats(params);
    }
    
    /**
   	 *통계자료를 등록 한다
   	 * @param params
   	 */
	public void insertStats(FtMap params) {
		statsMapper.insertStats(params);
	}

	/**
   	 *통계자료를 삭제 한다
   	 * @param params
   	 */
	public void deleteStats(FtMap params) {
		statsMapper.deleteStats(params);
	}

       
    
      
    /**
   	 * 사업정보 조회 자료 엑셀 다운로드 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public List listStatsExcl( FtMap params) throws Exception {
	          	
		 List list = statsMapper.listStatsExcl(params);       	
		 return list;
	 }
   	
    
    
}
