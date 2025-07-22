package com.futechsoft.framework.security.mapper;

import java.util.List;

import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.auth.vo.Path;
import com.futechsoft.admin.auth.vo.PathRole;
import com.futechsoft.admin.menu.vo.Menu;
import com.futechsoft.admin.user.vo.User;
import com.futechsoft.admin.user.vo.UserRole;
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
@Mapper("framework.security.mapper.SecurityMapper")
public interface SecurityMapper {

	public User getUserInfo(String userId) throws Exception;

	public User getUserInfoByDn(String userDn) throws Exception;

	public User getUserInfoBySsoId(String ssoId) throws Exception;

	public List<UserRole> getUserRoleList(String userId) throws Exception;

	public void resetFailCnt(String userId) throws Exception;

	public void plusFailCnt(String userId) throws Exception;

	public void disabledUser(String userId) throws Exception;

	public List<RoleMenu> getRoleMenu(long roleSeq) throws Exception;

	public List<Menu> getTopMenuList() throws Exception;

	public List<Menu> getSubMenuList(long menuSeq) throws Exception;

	public List<PathRole> getPathRoleList(long pathSeq) throws Exception;

	public List<Path> getAllPathList() throws Exception;


	public List<Menu> selectAllMenuList() throws Exception;

}
