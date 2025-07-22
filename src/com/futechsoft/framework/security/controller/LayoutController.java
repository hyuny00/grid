package com.futechsoft.framework.security.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futechsoft.framework.security.service.LayoutService;
import com.futechsoft.framework.security.vo.MenuVO;

@Controller
public class LayoutController {


	@Autowired
	private LayoutService layoutService;

	@RequestMapping("/framework/layout/header")
	public String topMenu(HttpServletRequest request) throws Exception {
		return "framework/layout/header";
	}
	
	

	@RequestMapping("/framework/layout/leftMenu")
	public String leftMenu(HttpServletRequest request) throws Exception {
		return "framework/layout/leftMenu";
	}

	@RequestMapping("/framework/layout/adminHeader")
	public String adminHeader(HttpServletRequest request) throws Exception {
		return "framework/layout/adminHeader";
	}

	@RequestMapping("/framework/layout/adminLeftMenu")
	public String adminLeftMenu(HttpServletRequest request) throws Exception {
		return "framework/layout/adminLeftMenu";
	}
	
	@GetMapping("/framework/layout/authMenuTree")
    public ResponseEntity<List<MenuVO>> getAuthMenuTree(@RequestParam(required = false) Long selectedMenuSeq) {
    	List<MenuVO> menuTree = layoutService.getAuthMenuTree(selectedMenuSeq);
        return ResponseEntity.ok(menuTree);
    }
}
