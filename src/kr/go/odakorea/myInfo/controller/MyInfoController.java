/**
 * 마이페이지
 */
package kr.go.odakorea.myInfo.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.inswave.wrm.util.Result;

import kr.go.odakorea.otcm.controller.OtcmController;
import kr.go.odakorea.myInfo.service.MyInfoService;


/**
* 마이페이지 관리하는 Controller 클래스이다
* @packageName    : kr.go.odakorea.myinfo.controller
* @fileName       : MyinfoController.java
* @description    :	마이페이지 관리하는 Controller 클래스이다
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.06.12        pwo              최초 생성
 */
@Controller
public class MyInfoController  extends AbstractController{
	@Resource(name = "myinfo.service.MyInfoService")
	private MyInfoService myInfoService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcmController.class);
	/**
	 * 마이페이지 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/myInfo/listMypage")
	public String listMypage(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = myInfoService.listMypage(pageble, params);
		
		request.setAttribute("mypageList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "myInfo/mypage";
	}
	
	
	
	/**
	 * 상세 화면으로 이동한다.
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/mypageDtl")
	public String mypageDtl(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfo/mypageDtl");
		return ViewInfo.REDIRECT_PAGE;
	}
	

	/**
	 * 목록 화면으로 이동한다.(더보기)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/mypageList")
	public String mypageList(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfo/mypageList");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 쪽지 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody  
	@RequestMapping("/myInfo/listNote")
	public String listNote(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = myInfoService.listNote(pageble, params);
		
		request.setAttribute("noteList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "myInfo/noteList";
	}
	
	
	
	
	/**
	 * 쪽지를 등록 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/insertNoteSndng")
	public String insertStats(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		myInfoService.insertNoteSndng(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfo/noteList");
		return ViewInfo.REDIRECT_PAGE;
	}
	

	/**
	 * 쪽지 엑셀 다운로드 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/myInfo/listNoteExcl")
	public String listStatsExcl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = myInfoService.listNoteExcl(params);
		
		request.setAttribute("noteList", pageList);
	
		return "myInfo/noteList";
	}
	
	
	/**
	 * 쪽지 상세 조회한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/myInfo/noteDtl")
	public String noteDtl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		FtMap  pageMap = myInfoService.noteDtl(params);
		
		request.setAttribute("noteDtl", pageMap);
	
		return "myInfo/noteDtl";
	}
	
	/**
	 * 쪽지를 등록 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/insertNote")
	public String insertNote(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		myInfoService.insertNote(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfo/noteList");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	/**  
	 * 쪽지 회신 화면으로 이동한다.
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/noteOcrn")
	public String noteOcrn(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfo/noteDtl");
		return ViewInfo.REDIRECT_PAGE;
	}
	

	/**
	 * 일정목록을 조회한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listSchdl")
	public String listSchdl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = myInfoService.listSchdl(params);
		
		request.setAttribute("schdlList", pageList);
	
		return "myInfo/schdlList";
	}
	
	
	/**
	 * 일정 상세 조회한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/myInfo/schdlDtl")
	public String schdlDtl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		FtMap  pageMap = myInfoService.schdlDtl(params);
		
		request.setAttribute("schdlDtl", pageMap);
	
		return "myInfo/schdlDtl";
	}
	
	
	/**
	 * 일정을 저장 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/insertSchdl")
	public String insertSchdl(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		myInfoService.insertSchdl(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfo/schdlDtl");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	
	/**
	 * 일정을 수정 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/updateSchdl")
	public String updateStats(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		myInfoService.updateStats(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/schdlDtl");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	
	
	/**
	 * 내정보를 조회한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/myInfo/myInfoDtl")
	public String myInfoDtl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		FtMap  pageMap = myInfoService.myInfoDtl(params);
		
		request.setAttribute("myInfoDtl", pageMap);
	
		return "myInfo/myInfoDtl";
	}
	
	
	/**
	 * 내정보를  수정 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/myInfo/updateMyInfo")
	public String updateMyInfo(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		myInfoService.updateMyInfo(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/myInfoDtl");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	
	
}
