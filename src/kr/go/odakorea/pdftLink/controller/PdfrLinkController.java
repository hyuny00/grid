/**
 * 유무상 연계 패키지
 */
package kr.go.odakorea.pdftLink.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.common.pagination.Page;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

import kr.go.odakorea.pdftLink.service.PdfrLinkService;

/**
* 유무상 연계를 관리하는 Controller 클래스이다
* @packageName    : kr.go.odakorea.pdftLink.controller
* @fileName       : PdfrLinkController.java
* @description    :	유무상 연계를 관리하는 Controller 클래스이다
* ===========================================================
* DATE              AUTHOR             NOTE
* -----------------------------------------------------------
* 2025.05.22        pdh       최초 생성
 */
@Controller
public class PdfrLinkController  extends AbstractController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfrLinkController.class);
	
	
	@Resource(name = "pdftLink.service.PdfrLinkService")
	private PdfrLinkService pdfrLinkService;
	
	
	/**
	 * ODA 사업목록을 조회한다
	 * @param pageble
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/listOdaBizInfo")
	public  Map<String, Object>  listOdaBizInfo(@RequestBody(required=false) Map<String, Object> map) throws Exception{
		
		
		FtMap params = getFtMap(map);
		
		Pageable pageable = new Pageable();
		pageable.setParam(params);
		
		
		
		LOGGER.debug("schInstCd..........", SecurityUtil.getMiniCd());
	
		params.put("userId", SecurityUtil.getUserId());
		
		params.put("schInstCd", SecurityUtil.getMiniCd());
		params.put("schYear", CommonUtil.getToday("yyyy"));
		params.put("schDiv", "01");
		
		
		Page<FtMap> page = pdfrLinkService.listOdaBizInfo(pageable, params);

		List<FtMap> resultList= page.getList();
		Map<String, Object> response = new HashMap<>();
	    response.put("data", resultList);
	    response.put("total",  page.getPageable().getTotalCount());


	    return response;
		
	}
	

	
	/**
	 * 사업간 연계를 요청한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/createBizLinkDmnd")
	public Map<String, Object> createBizLinkDmnd(Pageable pageble, HttpServletRequest request) throws Exception{
		
		
		
		return null;
		
	}
	
	
	/**
	 * 사업간 연계를 삭제한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/deleteBizLinkDmnd")
	public Map<String, Object> deleteBizLinkDmnd(@RequestBody Map<String, Object> map) throws Exception{
	
		return null;
		
	}
	
	
	
	/**
	 * 사압간 연계를 수정한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/updateBizLinkDmnd")
	public FtMap updateBizLinkDmnd(HttpServletRequest request) throws Exception{
		return null;
	}
	
	
	/**
	 * 사업간연계를 삭제한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/deleteBizLink")
	public FtMap deleteBizLink(HttpServletRequest request) throws Exception{
		return null;
	}
	
	
	
	/**
	 * 검토의견을 저장한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/saveOpnn")
	public FtMap saveOpnn(HttpServletRequest request) throws Exception{
		return null;
	}
	
	
	
	/**
	 * 검토의견을 삭제한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/deleteOpnn")
	public FtMap deleteOpnn(HttpServletRequest request) throws Exception{
		return null;
	}
	
	
	
	/**
	 * 사업간 연계를 확정/확정취소한다
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/updateBizLinkCfmtn")
	public FtMap  updateBizLinkCfmtn(HttpServletRequest request) throws Exception{
		return null;
	}
	
	
	/**
	 * 사업간 연계확정을 등록한다.
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/pdftLink/createBizLinkCfmtn")
	public FtMap  createBizLinkCfmtn(HttpServletRequest request) throws Exception{
		return null;
	}

	
}

