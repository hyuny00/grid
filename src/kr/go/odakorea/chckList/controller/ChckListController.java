package kr.go.odakorea.chckList.controller;

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

import kr.go.odakorea.chckList.service.ChckListService;

/**
 * 체크리스트를 관리하는 Controller 클래스
 * @packageName    : kr.go.odakorea.chckList.controller
 * @fileName       : ChckListController.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 */
@Controller
public class ChckListController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChckListController.class);
	
	@Resource(name = "chckList.service.ChckListService")
	ChckListService chckListService;
	
	/**
	 * 체크리스트 지표 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/chckList/listIdct")
	public String listIdct(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = chckListService.listIdct(pageble, params);
		
		request.setAttribute("chckListList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "chckList/listIdct";
	}
	
	
	/**
	 * 체크리스트 지표를 등록한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/chckList/createIdct")
	public String createIdct(HttpServletRequest request) throws Exception {
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		chckListService.createIdct(params);
		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/chckList/listIdct");
		return ViewInfo.REDIRECT_PAGE;
		
	}
	
	/**
	 * 체크리스트 지표를 수정한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/chckList/updateIdct")
	public String updateIdct(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		chckListService.updateIdct(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/chckList/getIdct");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 사업 체크리스트대상 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/chckList/listChckList")
    public String listChckList(Pageable pageble, HttpServletRequest request) throws Exception {

		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		Page<FtMap> page = chckListService.getListChckList(pageble, params);
		
		request.setAttribute("chckListList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "chckList/listChckList";
    }

    
	/**
	 * 사업 체크리스트대상 상세내용을 조회한다 
	 * @param request
	 * @return  jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/chckList/getChckList")
	public String getChckList(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		FtMap result = chckListService.getChckList(params);

		request.setAttribute("result", result);


		return "tiles:chckList/chckListForm";
	}
	
    /**
	 * 체크리스트내용을 등록한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping(value = "/chckList/createChckList")
	public String createChckList(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		chckListService.createChckList(params);

		request.setAttribute(ViewInfo.REDIRECT_URL, "/chckList/getListChckList");
		return ViewInfo.REDIRECT_PAGE;

	}


	/**
	 * 체크리스트내용을 수정한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/chckList/updateChckList")
	public String updateChckList(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		chckListService.updateChckList(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/chckList/getChckList");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 체크리스트내용을 삭제한다
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/chckList/deleteChckList")
	public String deleteChckList(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		chckListService.deleteChckList(params);

		request.setAttribute("message", "삭제되었습니다.");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/chckList/getListChckList");
		return ViewInfo.REDIRECT_PAGE;

	}
	

	/**
	 * 체크리스트 현황을 조회한다
	 * @param pageble
	 * @param request
	 * @return jsp경로
	 * @throws Exception
	 */
	@RequestMapping("/chckList/listChckListPrst")
	public String listChckListPrst(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = chckListService.listChckListPrst(pageble, params);
		
		request.setAttribute("chckListPrstList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "chckList/chckListPrstList";
	}

}
