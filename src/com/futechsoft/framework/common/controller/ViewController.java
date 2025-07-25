package com.futechsoft.framework.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

@Controller
public class ViewController  extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);

	@RequestMapping(value = "/common/view")
	public String commonPupup(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		return params.getString("view");
	}


	@RequestMapping(path = {"/common/{pathName1}", "/common/{pathName1}/{pathName2}",  "/common/{pathName1}/{pathName2}/{pathName3}" })
	public String commonPage(HttpServletRequest request, @PathVariable String  pathName1, @PathVariable Optional<String>  pathName2, @PathVariable Optional<String>  pathName3) throws Exception {


		String path="";


		path="/"+pathName1;

		if(pathName2.isPresent()) {
			path+="/"+pathName2.get();
		}

		if(pathName3.isPresent()) {
			path+="/"+pathName3.get();
		}

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		return "tiles:common/"+path;
	}




	/**
	 * 코드 목록을 가져온다
	 * @param request
	 * @return 코드
	 * @throws Exception
	 */
	@RequestMapping(value = "/common/selectCode")
	@ResponseBody
	public List<FtMap> selectCode(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		
		
		List<FtMap> codeList = null;
		

		String code= params.getString("code");
		int cdGroupSn= params.getInt("cdGroupSn");
		params.put("upCd", code);
		params.put("cdGroupSn", cdGroupSn);
		
		
		String schCodeDiv= params.getString("schCodeDiv");
		
		
		if(CommonUtil.nvl(schCodeDiv).equals("bizFldCd")) {
			
			if( params.getString("code").equals("")) {
				params.put("upCd", "-");
			}else {
				params.put("upCd", code);
			}
			codeList = getCommonService().selectBizFldCdList(params);
			
		}else if(CommonUtil.nvl(schCodeDiv).equals("ntnCd")) {
			params.put("schCntnt", "Y");
			params.put("schUpNtnCd", code);
			codeList = getCommonService().selectNtnSubCodeList(params);
			
		}else if(CommonUtil.nvl(schCodeDiv).equals("instCd")) {
			codeList = getCommonService().selectInstCdList(params);
			
		}else {
			codeList = getCommonService().selectCommonCodeList(params);
		}

		return codeList;

	}
	
	
	@RequestMapping(value = "/common/selectBizFldCdList")
	@ResponseBody
	public List<FtMap> selectBizFldCdList(HttpServletRequest request) throws Exception {

		FtMap params = super.getFtMap(request);
		
		List<FtMap> codeList= getCommonService().selectBizFldCdList(params);
		
		return codeList;

	}
	
	

	@RequestMapping(value = "/common/selectCodeMultiple")
	@ResponseBody
	public Map<String, Object> selectCodeMultiple(@RequestBody Map<String, Object> requestData) throws Exception {
	    
	 
	    FtMap params = getFtMap(requestData); 
	    
	    List<Map<String, Object>> requests = (List<Map<String, Object>>) params.get("requests");
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    if (requests != null) {
	        for (Map<String, Object> request : requests) {
	           
	        	String code = (String) request.get("code");
	            String cdGroupSn = (String) request.get("cdGroupSn");
	            String schCodeDiv = (String) request.get("schCodeDiv");
	            
	            
	            
	            params.put("upCd", code);
	    		params.put("cdGroupSn", cdGroupSn);
	    		
	            
	            // 기존 selectCode 서비스 로직 재사용
	           // List<FtMap> codeList = getCommonService().selectCommonCodeList(params);
	            
	          
	            
	            List<FtMap> codeList =null;
	            
	            if(CommonUtil.nvl(schCodeDiv).equals("bizFldCd")) {
	    			codeList = getCommonService().selectBizFldCdList(params);
	    		}else if(CommonUtil.nvl(schCodeDiv).equals("instCd")) {
	    			codeList = getCommonService().selectInstCdList(params);
	    		}else if(CommonUtil.nvl(schCodeDiv).equals("ntnCd")) {
	    			codeList = getCommonService().selectNtnCodeList(params);
	    		}else {
	    			codeList = getCommonService().selectCommonCodeList(params);
	    		}
	            
	            FtMap codeMap = super.getCommonService().selectCommonCodeMap(codeList);
	            
	            
	           
	            
	            resultMap.put(schCodeDiv, codeMap);
	        }
	    }
	    
	    return resultMap;
	}
		
	
	
	
	@RequestMapping(value = "/common/selectCodeListMultiple")
	@ResponseBody
	public Map<String, Object> selectCodeListMultiple(@RequestBody Map<String, Object> requestData) throws Exception {
	    
	 
	    FtMap params = getFtMap(requestData); 
	    
	    List<Map<String, Object>> requests = (List<Map<String, Object>>) params.get("requests");
	    Map<String, Object> resultMap = new HashMap<>();
	    
	    if (requests != null) {
	        for (Map<String, Object> request : requests) {
	            String code = (String) request.get("code");
	            String cdGroupSn = (String) request.get("cdGroupSn");
	            String codeDiv = (String) request.get("codeDiv");
	            
	            params.put("upCd", code);
	    		params.put("cdGroupSn", cdGroupSn);
	    		
	            
	            // 기존 selectCode 서비스 로직 재사용
	            List<FtMap> codeList = getCommonService().selectCommonCodeList(params);
	            
	            // select2 형태로 변환
	            List<Map<String, Object>> options = new ArrayList<>();
	            for (FtMap item : codeList) {
	                Map<String, Object> option = new HashMap<>();
	                option.put("value", item.getString("id"));
	                option.put("text", item.getString("text"));
	                options.add(option);
	            }
	            
	            resultMap.put(codeDiv, options);
	        }
	    }
	    
	    return resultMap;
	}
		
	
	
	
	
	
	
}
