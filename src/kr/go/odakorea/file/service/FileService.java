package kr.go.odakorea.file.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.go.odakorea.file.mapper.FileMapper;


/**
 * 파일을 관리하는 Service 클래스
 * @packageName    : kr.go.odakorea.file.service
 * @fileName       : FileService.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        pwo              최초 생성
 */
@Service("file.service.FileService")
public class FileService extends EgovAbstractServiceImpl {

	 @Resource(name="file.mapper.FileMapper")
	 private FileMapper fileMapper;

	 
	/**
	* 파일 자료를 조회한다
	* @param params
	* @return
	* @throws Exception
	*/
	public List listFileData( FtMap params) throws Exception {
	   	       	
		List list = fileMapper.listFileData(params);	
		return list;
	}
       
       
	/**
	 * 파일을 업로드 한다
	 * @param params
	 */
	public void insertFileUld(FtMap params) {
		fileMapper.insertFileUld(params);
	}
	   	
	/**
	* 한글서식을 다운로드 한다
	* @param params 
	*/
	public List listKornTmpltDwnld(FtMap params) {
		List list = fileMapper.listKornTmpltDwnld(params);	
		return list;
	}
	
	/**
	*  엑셀양식을 다운로드 한다
	* @param params 
	*/
	public List listExclFormDwnld(FtMap params) {
		List list = fileMapper.listExclFormDwnld(params);	
		return list;
	}
	
	/**
	 * 파일 저장 및 검증 
	 * @param params
	 */
	public void updateFileVrfc(FtMap params) {
		fileMapper.updateFileVrfc(params);
	}
	   	
	
	/**
   	 * 엑셀 다운로드 한다
   	 * @param params
   	 * @return
   	 * @throws Exception
   	 */
	 public List listFileExcl( FtMap params) throws Exception {
	          	
		 List list = fileMapper.listFileExcl(params);       	
		 return list;
	 }
	
	
	
}
