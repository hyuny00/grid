/**
 * 통합검색
 */
package kr.go.odakorea.intgSrch.controller;


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
import kr.go.odakorea.intgSrch.service.IntgSrchService;

/**
* 통합검색을 관리하는 Controller 클래스이다
* @packageName    : kr.go.odakorea.intgSrch.controller
* @fileName       : intgSrchController.java
* @description    :	통합검색을 관리하는 Controller 클래스이다
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.06.12        pwo              최초 생성
 */
@Controller
public class IntgSrchController  extends AbstractController{
	
	@Resource(name = "intgSrch.service.IntgSrchService")
	private IntgSrchService intgSrchService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OtcmController.class);
	/**
	 * 통합검색 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/intgSrch/listIntgSrch")
	public String listIntgSrch(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = intgSrchService.listIntgSrch(pageble, params);
		
		request.setAttribute("intgSrchList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/intgSrchList";
	}
	
	
	
	/**
	 * 통합검색 옵션 설정  조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/intgSrch/listIntgSrchOpt")
	public String listIntgSrchOpt(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = intgSrchService.listIntgSrchOpt(pageble, params);
		
		request.setAttribute("intgSrchOptList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/intgSrchList";
	}
	
	
	
	
	/**
	 * 통합검색 상세  조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/intgSrch/listIntgSrchDtl")
	public String listIntgSrchDtl(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = intgSrchService.listIntgSrchDtl(pageble, params);
		
		request.setAttribute("intgSrchDtlList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/intgSrchDtlList";
	}
	
	
}