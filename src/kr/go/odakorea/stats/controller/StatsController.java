/**
 * 통계
 */
package kr.go.odakorea.stats.controller;

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
import kr.go.odakorea.stats.service.StatsService;

/**
* 통계를 관리하는 Controller 클래스이다
* @packageName    : kr.go.odakorea.stats.controller
* @fileName       : StatsController.java
* @description    :	통계를 관리하는 Controller 클래스이다
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.12        pwo              최초 생성
 */
@Controller
public class StatsController  extends AbstractController{

	@Resource(name = "stats.service.StatsService")
	private StatsService statsService;

	private static final Logger LOGGER = LoggerFactory.getLogger(OtcmController.class);
	/**
	 * 사업정보 통계선정할 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listBizInfoStatsSlctn")
	public String listBizInfoStatsSlctn(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listBizInfoStatsSlctn(pageble, params);
		
		request.setAttribute("bizInfoStatsSlctnList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/bizInfoStatsSlctn";
	}
	
	
	/**
	 * 통계선정 현황 화면으로 이동한다.
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/stats/statsSlctnPrst")
	public String statsSlctnPrst(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/statsSlctnPrst");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	
	/**
	 * 사업정보 통계선정현황 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listStatsSlctnPrst")
	public String listStatsSlctnPrst(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listStatsSlctnPrst(pageble, params);
		
		request.setAttribute("statsSlctnPrstList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/statsSlctnPrst";
	}
	
	
	/**
	 * 통계관리 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listStatsMng")
	public String listStatsMng(Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listStatsMng(pageble, params);
		
		request.setAttribute("statsMngList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/statsMng";
	}
	
	
	/**
	 * 제출현황 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listSbmsnPrst")
	public String listSbmsnPrst (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listSbmsnPrst(pageble, params);
		
		request.setAttribute("sbmsnPrstList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/sbmsnPrst";
	}
	
	/**
	 * OECD자료관리 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listOecdDataMng")
	public String listOecdDataMng (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listOecdDataMng(pageble, params);
		
		request.setAttribute("oecdDataMngList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/oecdDataMng";
	}
	
	
	/**
	 * OECD자료업로드 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listOecdDataUld")
	public String listOecdDataUld (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listOecdDataUld(pageble, params);
		
		request.setAttribute("OecdDataUldList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/OecdDataUld";
	}
	
	
	/**
	 * 데이터검증 자료를 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listDataVrfc")
	public String listDataVrfc (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listDataVrfc(pageble, params);
		
		request.setAttribute("dataVrfcList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/dataVrfc";
	}
	
	
	/**
	 * 데이터검증목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listDataVrfcList")
	public String listDataVrfcList (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listDataVrfcList(pageble, params);
		
		request.setAttribute("dataVrfcList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/dataVrfcList";
	}
	
	
	
	/**
	 * OECD 자료현황을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listOecdDataPrst")
	public String listOecdDataPrst (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listOecdDataPrst(pageble, params);
		
		request.setAttribute("oecdDataPrstList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/oecdDataPrst";
	}
	
	
	/**
	 * 통계코드 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listStatsCd")
	public String listStatsCd (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listStatsCd(pageble, params);
		
		request.setAttribute("statsCdList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/statsCd";
	}
	
	
	/**
	 * 통계코드 매핑 목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listStatsCdMpng")
	public String listStatsCdMpng (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listStatsCdMpng(pageble, params);
		
		request.setAttribute("statsCdMpngList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/statsCdMpng";
	}
	
	
	/**
	 * 통계코드 이력을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody 
	@RequestMapping("/stats/listStatsCdHstry")
	public String listStatsCdHstry (Pageable pageble, HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		Page<FtMap> page = statsService.listStatsCdHstry(pageble, params);
		
		request.setAttribute("statsCdHstryList", page.getList());
		request.setAttribute("pageObject", page.getPageable());
		return "stats/statsCdHstry";
	}
	
	
	/**
	 * CRS엑셀 업로드
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listCrsExcl")
	public String listCrsExcl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = statsService.listCrsExcl(params);
		
		request.setAttribute("oecdDataMng", pageList);
	
		return "stats/oecdDataMng";
	}
	
	
	/**
	 * 로데이터 엑셀 업로드
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listDataExcl")
	public String listDataExcl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = statsService.listDataExcl(params);
		
		request.setAttribute("oecdDataMng", pageList);
	
		return "stats/oecdDataMng";
	}
	
	
	/**
	 * 통계자료를 수정 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/stats/updateStats")
	public String updateStats(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		statsService.updateStats(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/bizInfoStatsSlctn");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	
	/**
	 * 통계자료를 등록 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/stats/insertStats")
	public String insertStats(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		statsService.insertStats(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/bizInfoStatsSlctn");
		return ViewInfo.REDIRECT_PAGE;
	}

	/**
	 * 통계자료를 삭제 한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/stats/deleteStats")
	public String deleteStats(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		statsService.deleteStats(params);
		
		request.setAttribute("sendParams", "id");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/stats/bizInfoStatsSlctn");
		return ViewInfo.REDIRECT_PAGE;
	}
	
	/**
	 * 통계자료 엑셀 다운로드 조회한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/stats/listStatsExcl")
	public String listStatsExcl( HttpServletRequest request) throws Exception{
		// 권한 체크 필요할때(ROLE_ 붙여야함)
		if (SecurityUtil.hasAuth("ROLE_ADMIN")) {
			LOGGER.debug("ROLE_ADMIN");
		}
		
		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		List pageList = statsService.listStatsExcl(params);
		
		request.setAttribute("bizInfoStatsList", pageList);
	
		return "stats/bizInfoStatsSlctn";
	}
	
	
	
}
