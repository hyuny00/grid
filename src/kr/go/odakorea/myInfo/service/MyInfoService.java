package kr.go.odakorea.myInfo.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.myInfo.mapper.MyInfoMapper;


/**
 * 마이페이지 관리하는 Service 클래스
 * @packageName    : kr.go.odakorea.mypage.service
 * @fileName       : MyInfoService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Service("myinfo.service.MyInfoService")
public class MyInfoService extends EgovAbstractServiceImpl {

	 @Resource(name="myInfo.mapper.MyInfoMapper")
	 private MyInfoMapper myInfoMapper;

	 /**
	 *  마이페이지 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */

	public Page<FtMap> listMypage(Pageable pageable, FtMap params) throws Exception {
		
		List<FtMap> list = myInfoMapper.listMypage(pageable, params);
		long count = myInfoMapper.conuntListMypage(params);
		
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
	
	 /**
	 *  쪽지 목록을 조회한다
	 * @param pageable
	 * @param params
	 * @return
	 * @throws Exception
	 */

	public Page<FtMap> listNote(Pageable pageable, FtMap params) throws Exception {
		
		List<FtMap> list = myInfoMapper.listNote(pageable, params);
		long count = myInfoMapper.conuntListNote(params);
		
		Page<FtMap> page = new Page<FtMap>(pageable, list, count);
		return page;
	}
	
    
    /**
   	 * 쪽지를 등록 한다
   	 * @param params
   	 */
	public void insertNoteSndng(FtMap params) {
		myInfoMapper.insertNoteSndng(params);
	}
	
	
	
	/**
   	 * 쪽지 엑셀 다운로드 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public List listNoteExcl( FtMap params) throws Exception {
	          	
		 List list = myInfoMapper.listStatsExcl(params);       	
		 return list;
	 }
	 
	 /**
   	 * 쪽지 상세 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public FtMap noteDtl( FtMap params) throws Exception {
		 FtMap noteMap = myInfoMapper.noteDtl(params);       	
		 return noteMap;
	 }
	 
	 
	/**
	* 쪽지를 등록 한다
	* @param params
	*/
	public void insertNote(FtMap params) {
		myInfoMapper.insertNote(params);
	}

	 /**
   	 * 일정목록을 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public List listSchdl( FtMap params) throws Exception {
	          	
		 List list = myInfoMapper.listSchdl(params);       	
		 return list;
	 }
	
	 /**
   	 * 일정 상세 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public FtMap schdlDtl( FtMap params) throws Exception {
		 FtMap schdlDltMap = myInfoMapper.schdlDtl(params);       	
		 return schdlDltMap;
	 }
	 
	/**
	* 일정을 등록 한다
	* @param params
	*/
	public void insertSchdl(FtMap params) {
		myInfoMapper.insertSchdl(params);
	}
	 
	
	 /**
	 *일정을 수정 한다
	 * @param params
	 */
    public void updateStats(FtMap params) {
    	myInfoMapper.updateStats(params);
    }
	
    
    /**
   	 * 내정보를 조회한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public FtMap myInfoDtl( FtMap params) throws Exception {
		 FtMap myInfoDtlMap = myInfoMapper.myInfoDtl(params);       	
		 return myInfoDtlMap;
	 }
    
	 
	 
	/**
	 *내정보를 수정 한다
	 * @param params
	 */
    public void updateMyInfo(FtMap params) {
    	myInfoMapper.updateMyInfo(params);
    }
	    
}
