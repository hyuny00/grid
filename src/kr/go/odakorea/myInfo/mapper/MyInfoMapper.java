package kr.go.odakorea.myInfo.mapper;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

/**
 * 마이페이지 관리하는 Mapper 클래스
 * @packageName    : kr.go.odakorea.myInfo.mapper
 * @fileName       : MyInfoMapper.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Mapper("myInfo.mapper.MyInfoMapper")
public interface MyInfoMapper {

	
	/**
	 *  마이페이지 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listMypage(Pageable pageable, FtMap params);

	/**
	 *  마이페이지 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListMypage(FtMap params);

	
	
	/**
	 *  마이페이지 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 */

	List<FtMap> listNote(Pageable pageable, FtMap params);

	/**
	 *  마이페이지 목록 Count를 조회한다 
	 * @param params
	 * @return
	 */
	long conuntListNote(FtMap params);
	
	
	
	/**
	 * 통계자료를 등록한다
	 * @param params
	 */
    void insertNoteSndng(FtMap params);
	

	 /**
   	 * 사업정보 조회 자료 엑셀 다운로드 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listStatsExcl(FtMap params);

    
    /**
   	 * 쪽지 상세 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    FtMap noteDtl(FtMap params);
    
    
    /**
   	 * 쪽지를 등록한다
   	 * @param params
   	 */
    void insertNote(FtMap params);
    
    
    /**
   	 * 일정목록을 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    List listSchdl(FtMap params);

    /**
   	 * 일정 상세 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    FtMap schdlDtl(FtMap params);
    
    
    /**
   	 * 일정을 등록 한다
   	 * @param params
   	 */
    void insertSchdl(FtMap params);
    
    
    /**
	 * 일정을 수정한다
	 * @param params
	 */
    void updateStats(FtMap params);
    
    /**
   	 * 내정보를 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
    FtMap myInfoDtl(FtMap params);
    
    
	/**
	 * 내정보를 수정 한다 
	 * @param params
	 */
    void updateMyInfo(FtMap params);
    
    
}
