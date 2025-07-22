package com.futechsoft.admin.menu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.admin.menu.service.MenuService;
import com.futechsoft.admin.menu.vo.Menu;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.security.auth.ResourceMenuService;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

/**
 * <PRE>
 *
 * </PRE>
 *
 * @author futech
 * @version $Revision$
 */
@Controller
public class MenuController extends AbstractController {

	@Resource(name = "menu.service.MenuService")
	private MenuService menuService;

	@Resource(name = "framework.menu.service.ResourceMenuService")
	private ResourceMenuService resourceMenuService;

	@RequestMapping("/admin/menu/menuManager")
	public String menuManager(HttpServletRequest request) throws Exception {
		return "admin_tiles:admin/menu/menuManager";
	}

	@ResponseBody
	@RequestMapping("/admin/menu/getMenuList")
	public List<FtMap> getMenuListr(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("menuSeq")) || params.getString("menuSeq").equals("#")) {
			params.put("menuSeq", "-1");
		}
		List<Menu> menuList = menuService.getList(params);

		List<FtMap> menuListMap = new ArrayList<FtMap>();

		FtMap map = null;
		for (Menu menu : menuList) {
			map = new FtMap();

			map.put("id", String.valueOf(menu.getMenuSeq()));
			map.put("text", menu.getMenuNm());
			map.put("menuUrl", menu.getMenuUrl());
			map.put("upMenuNm", menu.getUpMenuNm());
			map.put("upMenuSeq", menu.getUpMenuSeq());
			map.put("useYn", menu.getUseYn());
			// map.put("openYn", menu.getOpenYn());
			map.put("etc", menu.getEtc());
			map.put("menuTypeCd", menu.getMenuTypeCd());
			map.put("children", menu.getSubMenuCnt() > 0 ? true : false);
			map.put("type", menu.getSubMenuCnt() > 0 ? "noLast" : "last");
			map.put("topMenuSeq", menu.getTopMenuSeq());

			menuListMap.add(map);
		}

		return menuListMap;
	}

	@ResponseBody
	@RequestMapping("/admin/menu/updateMenuOrd")
	public FtMap updateMenuOrd(HttpServletRequest request, @RequestParam(value = "menu_seqs[]") String[] menu_seqs) throws Exception {

		menuService.saveMenuOrd(menu_seqs);
		resourceMenuService.init();
		FtMap params = new FtMap();
		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/menu/saveMenu")
	public FtMap saveMenu(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		menuService.save(params);

		params.put("isSuccess", true);

		resourceMenuService.init();
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/menu/deleteMenu")
	public FtMap deleteMenu(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		boolean check = menuService.hasChildren(params);

		if (!check) {
			menuService.delete(params);
			params.put("isSuccess", true);

			resourceMenuService.init();

		} else {
			params.put("isSuccess", false);
			params.put("msg", "하위항목이 존재합니다.");
		}

		return params;

	}

}
