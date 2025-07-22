package kr.go.odakorea.exlncase.controller;

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

import kr.go.odakorea.exlncase.service.ExlnCaseService;

/**
 * 우수사례를 관리하는 Controller 클래스
 * @packageName    : kr.go.odakorea.exlncase.controller
 * @fileName       : ExlnCaseController.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Controller
public class ExlnCaseController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExlnCaseController.class);
	
	@Resource(name = "exlncase.service.ExlnCaseService")
	ExlnCaseService exlnCaseService;
	
	/**
	 * 우수사례 항목 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/exlnCase/listArtcl")
	public String listArtcl(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = exlnCaseService.listArtcl(pageble, params);
		
		request.setAttribute("exlnCaseList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "exlnCase/listArtcl";
	}
	
	
	/**
	 * 우수사례 항목을 등록한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/exlnCase/createArtcl")
	public String createArtcl(HttpServletRequest request) throws Exception {
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		exlnCaseService.createArtcl(params);
		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/exlnCase/listArtcl");
		return ViewInfo.REDIRECT_PAGE;
		
	}
	
	/**
	 * 우수사례 항목을 수정한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/exlnCase/updateArtcl")
	public String updateArtcl(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		exlnCaseService.updateArtcl(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/exlnCase/getArtcl");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 사업 우수사례대상 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/exlnCase/listExlnCase")
    public String listExlnCase(Pageable pageble, HttpServletRequest request) throws Exception {

		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		Page<FtMap> page = exlnCaseService.listExlnCase(pageble, params);
		
		request.setAttribute("exlnCaseList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "exlnCase/ExlnCaseList";
    }

    
	/**
	 * 사업 우수사례대상 상세내용을 조회한다 
	 * @param request
	 * @return  jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/exlnCase/getExlnCase")
	public String getExlnCase(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		FtMap result = exlnCaseService.getExlnCase(params);

		request.setAttribute("result", result);


		return "tiles:exlnCase/exlnCaseForm";
	}
	
    /**
	 * 우수사례내용을 등록한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/exlnCase/createExlnCase")
	public String createExlnCase(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		exlnCaseService.createExlnCase(params);

		request.setAttribute(ViewInfo.REDIRECT_URL, "/exlnCase/listExlnCase");
		return ViewInfo.REDIRECT_PAGE;

	}


	/**
	 * 우수사례내용을 수정한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/exlnCase/updateExlnCase")
	public String updateExlnCase(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		exlnCaseService.updateExlnCase(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/exlnCase/getExlnCase");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 우수사례내용을 삭제한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/exlnCase/deleteExlnCase")
	public String deleteExlnCase(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		exlnCaseService.deleteExlnCase(params);

		request.setAttribute("message", "삭제되었습니다.");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/exlnCase/listExlnCase");
		return ViewInfo.REDIRECT_PAGE;

	}
	

	/**
	 * 우수사례 현황을 조회한다
	 * @param pageble
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/exlnCase/listExlnCasePrst")
	public String listExlnCasePrst(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = exlnCaseService.listExlnCasePrst(pageble, params);
		
		request.setAttribute("exlnCasePrstList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "exlnCase/exlnCasePrstList";
	}

}
