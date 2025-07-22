package com.futechsoft.admin.auth.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.admin.auth.service.RoleService;
import com.futechsoft.admin.auth.vo.Path;
import com.futechsoft.admin.auth.vo.PathRole;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.security.auth.ResourceMetaService;
import com.futechsoft.framework.util.FtMap;

@Controller
public class PathController extends AbstractController {

	@Resource(name = "auth.service.RoleService")
	private RoleService roleService;

	@Resource(name = "framework.security.ResourceMetaService")
	private ResourceMetaService resourceMetaService;

	@RequestMapping("/admin/auth/pathManager")
	public String pathManager(HttpServletRequest request) throws Exception {

		return "admin_tiles:admin/auth/pathManager";
	}

	@ResponseBody
	@RequestMapping("/admin/auth/getPathList")
	public List<FtMap> getPathList(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("pathSeq")) || params.getString("pathSeq").equals("#")) {
			params.put("pathSeq", "-1");
		}
		List<Path> pathList = roleService.getPathList(params);

		List<FtMap> pathListMap = new ArrayList<FtMap>();

		FtMap map = null;
		for (Path path : pathList) {
			map = new FtMap();

			map.put("id", String.valueOf(path.getPathSeq()));
			map.put("text", path.getPath());
			map.put("pathNm", path.getPathNm());
			map.put("upPath", path.getUpPath());
			map.put("upPathSeq", path.getUpPathSeq());

			if (path.getPathSeq() != 0) {
				map.put("type", "030");
			}

			map.put("children", path.getSubCnt() > 0 ? true : false);

			pathListMap.add(map);
		}

		return pathListMap;
	}

	@RequestMapping("/admin/auth/authSelctor")
	public String roleSelctor(HttpServletRequest request) throws Exception {
		return "admin/auth/authSelctor";
	}

	@ResponseBody
	@RequestMapping("/admin/auth/getPathAuthList")
	public List<FtMap> getPathRoleList(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("roleSeq")) || params.getString("roleSeq").equals("#")) {
			params.put("roleSeq", "-1");
		}
	//	List<Auth> authList = authService.getList(params);

		List<PathRole> pathRoleList = roleService.getPathRoleList(params);

		List<FtMap> roleListMap = new ArrayList<FtMap>();

		FtMap map = null;
		boolean selected=true;
		for (PathRole pathRole : pathRoleList) {
			map = new FtMap();

			map.put("id", String.valueOf(pathRole.getRoleSeq()));
			map.put("text", pathRole.getRoleNm());
			map.put("upRoleNm", pathRole.getRoleNm());
			map.put("upRoleSeq", pathRole.getUpRoleSeq());
			map.put("parent", pathRole.getUpRoleSeq() == -1 ?"#" : pathRole.getUpRoleSeq());

			//map.put("useYn", auth.getUseYn());
			map.put("roleTypeCd", pathRole.getRoleTypeCd());
			//map.put("children", auth.getSubAuthCnt() > 0 ? true : false);
			map.put("type", pathRole.getRoleTypeCd());

			FtMap map1 = new FtMap();
			map1.put("selected", pathRole.getSelectYn().equals("Y")?true : false);
			map.put("state", map1);
			if(pathRole.getSelectYn().equals("N")) {
				selected=false;
			}

			roleListMap.add(map);
		}

		if(!selected) {
			roleListMap.get(0).put("selected", "N");
			//map1.put("selected", "N");
		}
		System.out.println("authListMap....."+roleListMap);

		return roleListMap;
	}

	@ResponseBody
	@RequestMapping("/admin/auth/updatePathOrd")
	public FtMap updateMenuOrd(HttpServletRequest request, @RequestParam(value = "path_seqs[]") String[] path_seqs)
			throws Exception {

		roleService.savePathOrd(path_seqs);

		FtMap params = new FtMap();
		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/auth/savePath")
	public FtMap savePath(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		roleService.savePath(params);

		resourceMetaService.createSecurityMetaDataSource();

		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/auth/deletePath")
	public FtMap deletePath(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		roleService.deletePath(params);

		resourceMetaService.createSecurityMetaDataSource();

		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/auth/savePathAuth")
	public FtMap savePathRole(HttpServletRequest request, @RequestBody FtMap params) throws Exception {

		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) params.get("role_seqs");
		roleService.savePathRole(params, list);

		resourceMetaService.createSecurityMetaDataSource();

		params.put("isSuccess", true);

		return params;

	}

}
