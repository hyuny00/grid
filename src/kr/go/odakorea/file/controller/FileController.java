/**
 * 파일
 */
package kr.go.odakorea.file.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.framework.common.constant.ViewInfo;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;
import com.google.common.net.HttpHeaders;
import com.inswave.wrm.util.Result;

import kr.go.odakorea.otcm.controller.OtcmController;
import kr.go.odakorea.file.service.FileService;

/**
* 파일을 관리하는 Controller 클래스이다
* @packageName    : kr.go.odakorea.file.controller
* @fileName       : FileController.java
* @description    :	파일을 관리하는 Controller 클래스이다
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.12        pwo              최초 생성
 */
@Controller
public class FileController  extends AbstractController{

	@Resource(name = "file.service.FileService")
	private FileService fileService;

	private static final Logger LOGGER = LoggerFactory.getLogger(OtcmController.class);
	/**
	 * 파일 자료를 조회한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/file/listFileData")
	public String listFileData( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = fileService.listFileData(params);
		
		request.setAttribute("FileDataList", pageList);
	
		return "stats/fileData";
	}
	

	
	/**
	 * 파일을 업로드 한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/file/insertFileUld")
	public String insertFileUld( HttpServletRequest request) throws Exception{
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		fileService.insertFileUld(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/fileData");
		return ViewInfo.REDIRECT_PAGE;
	}
		
	
	/**
	 * 한글서식을 다운로드 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/file/listKornTmpltDwnld")
	public String listKornTmpltDwnld( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = fileService.listKornTmpltDwnld(params);
		
		request.setAttribute("kornTmpltDwnldList", pageList);
	
		return "stats/fileData";
	}
	
	
	/**
	 * 엑셀양식을 다운로드 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/file/listExclFormDwnld")
	public String listExclTmpltDwnld( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = fileService.listExclFormDwnld(params);
		
		request.setAttribute("exclFormDwnldList", pageList);
	
		return "stats/fileData";
	}
	
	
    /**
	 * 파일 저장 및 검증 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/file/updateFileVrfc")
	public String updateFileVrfc(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		fileService.updateFileVrfc(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/fileData");
		return ViewInfo.REDIRECT_PAGE;
	}
	
    
	/**
	 * 엑셀 다운로드 한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/file/listFileExcl")
	public String listFileExcl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = fileService.listFileExcl(params);
		
		request.setAttribute("fileDataList", pageList);
	
		return "file/fileData";
	}
	
	/**
	 * 한글 파일 다운로드 한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/file/dwnldKornFile")
	public ResponseEntity<Resource> dwnldKornFile( HttpServletRequest request) throws Exception{
		String filename = request.getParameter("fileName");
				
		Path filePath = Paths.get("/path/file", filename);
	   // UrlResource resource = new UrlResource(filePath.toUri());
		
	    Resource resource =  (Resource) new FileSystemResource(filePath.toFile());
	    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\"" + filename + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);   

		
	}
	
	
	
	/**
	 * 파일 다운로드 한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/file/dwnldFile")
	public ResponseEntity<Resource> dwnldFile( HttpServletRequest request) throws Exception{
		String filename = request.getParameter("fileName");
				
		Path filePath = Paths.get("/path/file", filename);
	   // UrlResource resource = new UrlResource(filePath.toUri());
		
	    Resource resource =  (Resource) new FileSystemResource(filePath.toFile());
	    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\"" + filename + "\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);   

		
	}
	
	
	
	
	
}
