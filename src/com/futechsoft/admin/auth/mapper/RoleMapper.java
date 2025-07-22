package com.futechsoft.admin.auth.mapper;

import java.util.List;

import com.futechsoft.admin.auth.vo.Role;
import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.auth.vo.Path;
import com.futechsoft.admin.auth.vo.PathRole;
import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.util.FtMap;

/**
 * <PRE>
 *
 * </PRE>
 *
 * @author futech
 * @version $Revision$
 */
@Mapper("auth.mapper.RoleMapper")
public interface RoleMapper {

	public List<Role> getRoleList(FtMap params);

	public List<Role> getRoleCdList();

	public List<RoleMenu> getRoleMenuList(FtMap params);

	public List<Path> getPathList(FtMap params);

	public List<PathRole> getPathRoleList(FtMap params);
	
	 
	public void insertRole(FtMap params) throws Exception;
	public void updateRole(FtMap params) throws Exception;
	public void insertPath(FtMap params) throws Exception;
	public void updatePath(FtMap params) throws Exception;
	
	
	
	public void deleteUserRole(FtMap params) throws Exception;
	public void deleteRoleMenu(FtMap params) throws Exception;
	public void deleteRole(FtMap params) throws Exception;
	public void deletePathRoleByRoleSeq(FtMap params) throws Exception;
	
	int getRoleCount(FtMap params) throws Exception;
	
	public void insertRoleMenu(FtMap params) throws Exception;
	
	public void updatePathOrd(FtMap params) throws Exception;
	
	
	
	public void deletePathRoleByPathSeq(FtMap params) throws Exception;
	public void deletePath(FtMap params) throws Exception;
	
 	
	public void insertPathRole(FtMap params) throws Exception;
}
