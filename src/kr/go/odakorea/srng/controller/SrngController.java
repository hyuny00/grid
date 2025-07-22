package kr.go.odakorea.srng.controller;

import java.util.Map;

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

import kr.go.odakorea.srng.service.SrngService;

/**
 * 심사를 관리하는 controller 클래스
 * @packageName    : kr.go.odakorea.srng.controller
 * @fileName       : SrngController.java
 * @description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025.05.21        Yeonho Kim       최초 생성
 * 2025.06.12        Yeonho Kim       method update
 */
@Controller
public class SrngController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SrngController.class);
	
	@Resource(name = "srng.service.SrngService")
	SrngService srngService;
	
    //----------------------------------심사지표  Start------------------------------------------//
	
	/**
	 * 심사 지표 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/srng/listIdct")
	public String listIdct(Pageable pageble, HttpServletRequest request) throws Exception {
		
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = srngService.listIdct(pageble, params);
		
		request.setAttribute("SrngList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "srng/listIdct";
	}
	
	
	/**
	 * 심사 지표를 등록한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/srng/createIdct")
	public String createIdct(HttpServletRequest request) throws Exception {
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		srngService.createIdct(params);
		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/srng/listIdct");
		return ViewInfo.REDIRECT_PAGE;
		
	}
	
	/**
	 * 심사 지표를 수정한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/srng/updateIdct")
	public String updateIdct(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		srngService.updateIdct(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/srng/getIdct");
		return ViewInfo.REDIRECT_PAGE;
	}

    //----------------------------------심사지표 End------------------------------------------//
    
    
    //----------------------------------심사  Start------------------------------------------//
	/**
	 * 사업 심사대상 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
    @RequestMapping("/srng/listSrng")
    public String listSrng(Pageable pageble, HttpServletRequest request) throws Exception {

		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		Page<FtMap> page = srngService.listSrng(pageble, params);
		
		request.setAttribute("srngList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "srng/SrngList";
    }

	/**
	 * 사업 심사대상 상세내용을 조회한다 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/srng/getSrng")
	public String getSrng(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		Map result = srngService.getSrng(params);

		request.setAttribute("result", result);


		return "tiles:srng/srngForm";
	}

    
    /**
     * 심사내용을 등록한다(전략부합성 심사 대상 사업 등록)
     * @param request
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "/srng/createSrng")
	public String createSrng(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		srngService.createSrng(params);
//		for(File file : FileList ) {
//			// commService.saveFile(file);
//			
//		}

		request.setAttribute(ViewInfo.REDIRECT_URL, "/srng/listSrng");
		return ViewInfo.REDIRECT_PAGE;

	}

	/**
	 * 심사내용을 수정한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/srng/updateSrng")
	public String updateSrng(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		srngService.updateSrng(params);

		// 조회할 key값세팅. 여러개 세팅시: request.setAttribute("sendParams","id,name");, 검색조건,페이지번호등은 세팅할 필요없음.
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/srng/getSrng");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 심사내용을 삭제한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/srng/deleteSrng")
	public String deleteSrng(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		srngService.deleteSrng(params);

		request.setAttribute("message", "삭제되었습니다.");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/srng/listSrng");
		return ViewInfo.REDIRECT_PAGE;

	}

    //----------------------------------심사 End------------------------------------------//
    

}
