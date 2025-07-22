package com.futechsoft.admin.auth.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.admin.auth.service.RoleService;
import com.futechsoft.admin.auth.vo.Role;
import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.menu.service.MenuService;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.security.auth.ResourceMenuService;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

@Controller
public class RoleController extends AbstractController {

	@Resource(name = "auth.service.RoleService")
	private RoleService roleService;

	@Resource(name = "menu.service.MenuService")
	private MenuService menuService;

	@Resource(name = "framework.menu.service.ResourceMenuService")
	private ResourceMenuService resourceMenuService;

	@RequestMapping("/admin/auth/authManager")
	public String menuManager(HttpServletRequest request) throws Exception {

		return "admin_tiles:admin/auth/authManager";
	}

	@ResponseBody
	@RequestMapping("/admin/auth/getAuthList")
	public List<FtMap> getRoleList(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("roleSeq")) || params.getString("roleSeq").equals("#")) {
			params.put("roleSeq", "-1");
			params.put("useYn", "Y");
		}
		List<Role> roleList = roleService.getList(params);

		List<FtMap> roleListMap = new ArrayList<FtMap>();

		FtMap map = null;
		for (Role role : roleList) {
			map = new FtMap();

			map.put("id", String.valueOf(role.getRoleSeq()));
			map.put("text", role.getRoleNm());
			map.put("upRoleNm", role.getUpRoleNm());
			map.put("upRoleSeq", role.getUpRoleSeq());
			map.put("useYn", role.getUseYn());
			map.put("roleTypeCd", role.getRoleTypeCd());
			map.put("roleCd", role.getRoleCd());

			map.put("children", role.getSubRoleCnt() > 0 ? true : false);

			map.put("type", role.getRoleTypeCd());

			/*
			 * if(auth.getAuthSeq() == params.getLong("selectedAuthSeq")) { FtMap map1 = new
			 * FtMap(); map1.put("selected", true); map.put("state", map1); }
			 */

			roleListMap.add(map);
		}

		return roleListMap;
	}

	@RequestMapping("/admin/auth/menuSelctor")
	public String menuSelctor(HttpServletRequest request) throws Exception {

		return "admin/auth/menuSelctor";
	}

	@ResponseBody
	@RequestMapping("/admin/menu/getAuthMenuList")
	public List<FtMap> getRoleMenuList(HttpServletRequest request) throws Exception {
		
		

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("menuSeq")) || params.getString("menuSeq").equals("#")) {
			params.put("menuSeq", "-1");
		}
		//List<Menu> menuList = menuService.getList(params);

		List<RoleMenu> roleMenuList = roleService.getRoleMenuList(params);

		List<FtMap> menuListMap = new ArrayList<FtMap>();

		FtMap map = null;

		List<Long> noRoleMenuSeqList= new ArrayList<Long>();

		for (RoleMenu roleMenu : roleMenuList) {

			if(roleMenu.getNoRoleCount() >0 ) {
				long upMenuSeq = roleMenu.getUpMenuSeq();
				noRoleMenuSeqList.add(upMenuSeq);
			}
		}

		for (RoleMenu roleMenu : roleMenuList) {

			map = new FtMap();

			map.put("id", String.valueOf(roleMenu.getMenuSeq()));
			map.put("text", roleMenu.getMenuNm());
			map.put("menuUrl", roleMenu.getMenuUrl());
			map.put("upMenuSeq", roleMenu.getUpMenuSeq());
			map.put("parent", roleMenu.getUpMenuSeq() == -1 ?"#" : roleMenu.getUpMenuSeq());
			map.put("menuTypeCd", roleMenu.getMenuTypeCd());
			//map.put("type", authMenu.getMenuTypeCd());

			map.put("type", roleMenu.getSubMenuCnt() > 0 ? "noLast" : "last");

			FtMap map1 = new FtMap();

			if(noRoleMenuSeqList.contains( roleMenu.getMenuSeq())  && roleMenu.getSelectYn().equals("Y") ){
				map1.put("selected", false);
			}else if ( roleMenu.getNoRoleCount() > 0  && roleMenu.getSelectYn().equals("Y") ){
				map1.put("selected", false);
			}else if ( roleMenu.getNoRoleCount() == 0  && roleMenu.getSelectYn().equals("Y") ){
				map1.put("selected", true);
			}

			map.put("state", map1);

			menuListMap.add(map);

		}

		return menuListMap;
	}

	@ResponseBody
	@RequestMapping("/admin/auth/saveAuth")
	public FtMap saveRole(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		
		params.put("userNo", SecurityUtil.getUserNo());

		//if(!params.getString("authCd").startsWith(AuthConstant.ROLE_PREFIX)) params.put("authCd", AuthConstant.ROLE_PREFIX+params.getString("authCd"));
		roleService.save(params);

		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/auth/deleteAuth")
	public FtMap deleteMenu(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());
		boolean check = roleService.hasChildren(params);

		if (!check) {
			roleService.delete(params);
			params.put("isSuccess", true);
		} else {
			params.put("isSuccess", false);
			params.put("msg", "하위항목이 존재합니다.");
		}

		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/auth/saveAuthMenu")
	public FtMap saveRoleMenu(HttpServletRequest request, @RequestBody FtMap params) throws Exception {

		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) params.get("menu_seqs");
		roleService.saveRoleMenu(params, list);

		resourceMenuService.init();

		params.put("isSuccess", true);

		return params;

	}

}
