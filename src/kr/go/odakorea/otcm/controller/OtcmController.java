package kr.go.odakorea.otcm.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.futechsoft.framework.common.constant.ViewInfo;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

import kr.go.odakorea.otcm.service.OtcmService;

/**
 * 성과를 관리하는 Controller 클래스
 * @packageName    : kr.go.odakorea.otcm.controller
 * @fileName       : OtcmController.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Controller
public class OtcmController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OtcmController.class);
	
	@Resource(name = "otcm.service.OtcmService")
	OtcmService otcmService;
	
	

	/**
	 * 성과 목표 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/listOtcmGoal")
	public String listOtcmGoal(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = otcmService.listOtcmGoal(pageble, params);
		
		request.setAttribute("otcmList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "otcm/getIdctList";
	}
	
	
	/**
	 * 성과 목표를 등록한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/otcm/createOtcmGoal")
	public String createOtcmGoal(HttpServletRequest request) throws Exception {
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		otcmService.createOtcmGoal(params);
		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/getIdctList");
		return ViewInfo.REDIRECT_PAGE;
		
	}
	
	/**
	 * 성과 목표를 수정한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/updateOtcmGoal")
	public String updateOtcmGoal(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		otcmService.updateOtcmGoal(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/getIdct");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	/**
	 * 성과 목표를 삭제한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/deleteOtcmGoal")
	public String deleteOtcmGoal(HttpServletRequest request) throws Exception {
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		otcmService.deleteOtcmGoal(params);
		
		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/getIdct");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	
	/**
	 * 성과 지표 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/listPi")
	public String listPi(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);    
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = otcmService.listPi(pageble, params);
		
		request.setAttribute("otcmList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "otcm/getIdctList";
	}
	
	
	/**
	 * 성과 지표를 등록한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/otcm/createIdct")
	public String createIdct(HttpServletRequest request) throws Exception {
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		otcmService.createIdct(params);
		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/getIdctList");
		return ViewInfo.REDIRECT_PAGE;
		
	}
	
	/**
	 * 성과 지표를 수정한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/updateIdct")
	public String updateIdct(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		otcmService.updateIdct(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/getIdct");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 사업 성과대상 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/otcm/listOtcm")
    public String listOtcm(Pageable pageble, HttpServletRequest request) throws Exception {

		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		Page<FtMap> page = otcmService.listOtcm(pageble, params);
		
		request.setAttribute("otcmList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "otcm/otcmList";
    }

    
    /**
     * 성과내용을 등록한다
     * @param request
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "/otcm/createOtcm")
	public String createOtcm(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		otcmService.createOtcm(params);

		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/listOtcm");
		return ViewInfo.REDIRECT_PAGE;

	}

	/**
	 * 사업 성과대상 상세내용을 조회한다 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/getOtcm")
	public String getOtcm(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		FtMap result = otcmService.getOtcm(params);

		request.setAttribute("result", result);


		return "tiles:otcm/otcmForm";
	}

	/**
	 * 성과내용을 수정한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/updateOtcm")
	public String updateOtcm(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		otcmService.updateOtcm(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/getOtcm");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 성과내용을 삭제한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/deleteOtcm")
	public String deleteOtcm(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		otcmService.deleteOtcm(params);

		request.setAttribute("message", "삭제되었습니다.");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/otcm/listOtcm");
		return ViewInfo.REDIRECT_PAGE;

	}
	
	
    /**
	 * 성과 현황(current situation)을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/otcm/listOtcmPrst")
	public String listOtcmPrst(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = otcmService.listOtcmPrst(pageble, params);
		
		request.setAttribute("otcmPrstList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "review/otcmPrstList";
	}

}
