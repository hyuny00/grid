package com.futechsoft.framework.common.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;

import com.futechsoft.framework.common.service.CommonService;
import com.futechsoft.framework.excel.ExcelHelper;
import com.futechsoft.framework.security.event.ResourceMenuEventListener;
import com.futechsoft.framework.util.FtMap;


public abstract class AbstractController {

	@Autowired
	@Qualifier("framework.security.auth.ResourceMenuEventListener")
	private ResourceMenuEventListener resourceMenuEventListener;

	@Autowired
	@Qualifier("framework.common.service.CommonService")
	private CommonService commonService;

	@Autowired
	private ExcelHelper excelHelper;

	@Value("${excel.template.path}")
	private String templatePath;
	
	@Value("${ap.inOutDiv}")
	private String inOutDiv;
	

	protected FtMap getFtMap(HttpServletRequest request) throws Exception {
		
		
		FtMap params = new FtMap(request.getParameterMap());
		
		if(inOutDiv.equals("in")) {
			params.put("filePstnSecd", "01");
		}else if(inOutDiv.equals("out")) {
			params.put("filePstnSecd", "02");
		}else {
			 throw new Exception();
		}
		
		return params;
	}


	
	
	
	protected FtMap getFtMap(@RequestBody Map<String, Object> map) throws Exception {
		FtMap params = new FtMap();
		params.setFtMap(map);
		

		if(inOutDiv.equals("in")) {
			params.put("filePstnSecd", "01");
		}else if(inOutDiv.equals("out")) {
			params.put("filePstnSecd", "02");
		}else {
			 throw new Exception();
		}
		
		
		
		return params;
	}
	
	protected List<String> getListParam(FtMap params,String s) {
		
		String temp = params.getString(s);
		List<String> list =  new ArrayList<>();
		if(!"".equals(temp)) {
			list = Arrays.asList(temp.split(","));
		}
		return list;
	}
	

	/*
	 * protected void loadMenuInfo(HttpServletRequest request) throws Exception {
	 * List<Menu> menuList =resourceMenuEventListener.getAllMenuList();
	 * 
	 * FtMap menuCode = commonService.selectMenuMap(menuList);
	 * request.setAttribute("menuCode", menuCode); }
	 */

	protected CommonService getCommonService() {
		return commonService;
	}

	protected ExcelHelper getExcelHelper() {
		return excelHelper;
	}

	protected String getExceltemplatePath(HttpServletRequest request) {
		return templatePath;
	}

	

}
