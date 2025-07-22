package com.futechsoft.admin.user.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.admin.auth.service.RoleService;
import com.futechsoft.admin.user.service.UserService;
import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.common.constant.ViewInfo;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.common.page.Page;
import com.futechsoft.framework.common.page.Pageable;
import com.futechsoft.framework.security.auth.ResourceMetaService;
import com.futechsoft.framework.security.crypto.Sha256PasswordEncoder;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

@Controller
public class UserController extends AbstractController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	

	@Resource(name = "user.service.UserService")
	UserService service;

	@Resource(name = "auth.service.RoleService")
	private RoleService roleService;

	@Resource(name = "framework.security.ResourceMetaService")
	private ResourceMetaService resourceMetaService;

	@RequestMapping("/admin/user/userList")
	public String list(Pageable pageble, HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		
		
		System.out.println(params.toString());
		
		
		params.put("userNo", SecurityUtil.getUserNo());

		Page<FtMap> page = service.getPageList(pageble, params);

		request.setAttribute("list", page.getList());
		request.setAttribute("pageObject", page.getPageable());

		return "admin_tiles:admin/user/userList";
		
		//return "main";
	}

	@RequestMapping("/admin/user/userForm")
	public String userForm(Pageable pageble, HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		return "admin_tiles:admin/user/userDetail";
	}

	@RequestMapping("/admin/user/userDetail")
	public String userDetail(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		FtMap result = service.detail(params);

		request.setAttribute("result", result);
		return "admin_tiles:admin/user/userDetail";
	}

	@ResponseBody
	@RequestMapping("/admin/user/getUserAuthList")
	public List<FtMap> getUserRoleList(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("roleSeq")) || params.getString("roleSeq").equals("#")) {
			params.put("roleSeq", "-1");
		}
		//List<Auth> authList = authService.getList(params);

		List<UserRole> userRoleList = service.getUserRoleList(params);

		List<FtMap> roleListMap = new ArrayList<FtMap>();

		FtMap map = null;
		for (UserRole userRole : userRoleList) {
			map = new FtMap();

			map.put("id", String.valueOf(userRole.getRoleSeq()));
			map.put("text",userRole.getRoleNm());
			map.put("roleCd", userRole.getRoleCd());
			map.put("roleSeq", userRole.getRoleSeq());
			map.put("uproleSeq", userRole.getUpRoleSeq());

			map.put("parent", userRole.getUpRoleSeq() == -1 ?"#" : userRole.getUpRoleSeq());

			map.put("roleTypeCd", userRole.getRoleTypeCd());
			map.put("type", userRole.getRoleTypeCd());

			FtMap map1 = new FtMap();
			map1.put("selected", userRole.getSelectYn().equals("Y")?true : false);
			map.put("state", map1);


			roleListMap.add(map);
		}

		return roleListMap;
	}

	@RequestMapping("/admin/user/saveUser")
	public String saveUser(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		params.put("userPwd", passwordEncoder.encode(params.getString("userPwd")));

		service.create(params);

		request.setAttribute(ViewInfo.REDIRECT_URL, "/admin/user/userList");

		return ViewInfo.REDIRECT_PAGE;
	}

	@RequestMapping("/admin/user/updateUser")
	public String updateUser(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		params.put("userPwd", passwordEncoder.encode(params.getString("userPwd")));
		service.update(params);

		request.setAttribute("sendParams", "userNo");
		request.setAttribute(ViewInfo.REDIRECT_URL, "/admin/user/userDetail");

		return ViewInfo.REDIRECT_PAGE;
	}

	@RequestMapping("/admin/user/deleteUser")
	public String deleteUser(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		service.delete(params);

		request.setAttribute(ViewInfo.REDIRECT_URL, "/admin/user/userList");
		return ViewInfo.REDIRECT_PAGE;
	}
}
