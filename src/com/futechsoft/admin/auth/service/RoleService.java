package com.futechsoft.admin.auth.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futechsoft.admin.auth.mapper.RoleMapper;
import com.futechsoft.admin.auth.vo.Role;
import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.auth.vo.Path;
import com.futechsoft.admin.auth.vo.PathRole;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

@Service("auth.service.RoleService")
public class RoleService {

	@Resource(name = "auth.mapper.RoleMapper")
	private RoleMapper roleMapper;


	public List<Role> getList(FtMap params) throws Exception {

		return roleMapper.getRoleList(params);
	}

	@Transactional
	public void save(FtMap params) throws Exception {
		System.out.println("roleManager::"+params);
		if (params.getString("mode").equals("new")) {
			roleMapper.insertRole(params);
		} else {
			roleMapper.updateRole(params);
		}

	}

	@Transactional
	public void saveRoleMenu(FtMap params, ArrayList<String> menuSeqs) throws Exception {

		
		roleMapper.deleteRoleMenu(params);
		params.put("userNo", SecurityUtil.getUserNo());
		
		for (int i = 0; i < menuSeqs.size(); i++) {

			if (menuSeqs.get(i).equals("#") )
				continue;

			params.put("menuSeq", menuSeqs.get(i));
			
		

			roleMapper.insertRoleMenu(params);
		}

	}

	public List<RoleMenu> getRoleMenuList(FtMap params) throws Exception {
		return roleMapper.getRoleMenuList(params);
	}

	public List<Path> getPathList(FtMap params) throws Exception {
		return roleMapper.getPathList(params);
	}

	public List<PathRole> getPathRoleList(FtMap params) throws Exception {
		return roleMapper.getPathRoleList(params);
	}

	@Transactional
	public void savePath(FtMap params) throws Exception {

		params.put("userNo", SecurityUtil.getUserNo());
		if (params.getString("mode").equals("new")) {
			params.put("pathOrd", 99);
			roleMapper.insertPath(params);
		} else {
			roleMapper.updatePath(params);
		}
	}

	@Transactional
	public void deletePath(FtMap params) throws Exception {

		roleMapper.deletePathRoleByPathSeq(params);
		roleMapper.deletePath(params);

	}

	@Transactional
	public void savePathRole(FtMap params, ArrayList<String> roleSeqs) throws Exception {
		
		roleMapper.deletePathRoleByPathSeq(params);
		params.put("userNo", SecurityUtil.getUserNo());
		
		for (int i = 0; i < roleSeqs.size(); i++) {
			if (roleSeqs.get(i).equals("#") || roleSeqs.get(i).equals("0"))
				continue;
			params.put("roleSeq", roleSeqs.get(i));
		
			roleMapper.insertPathRole(params);
		}

	}

	@Transactional
	public void savePathOrd(String[] menuSeqs) throws Exception {

		FtMap params = new FtMap();
		for (int i = 0; i < menuSeqs.length; i++) {
			params.put("pathSeq", menuSeqs[i]);
			params.put("pathOrd", i);
			
			roleMapper.updatePathOrd(params);
		}

	}

	public boolean hasChildren(FtMap params) throws Exception {

		params.put("upRoleSeq", params.getString("roleSeq"));
		int count = roleMapper.getRoleCount(params);

		if (count == 0)
			return false;
		else
			return true;

	}

	@Transactional
	public void delete(FtMap params) throws Exception {
		
		roleMapper.deleteUserRole(params);
		roleMapper.deleteRoleMenu(params);
		roleMapper.deleteRole(params);
		roleMapper.deletePathRoleByRoleSeq(params);

	}
	

}
