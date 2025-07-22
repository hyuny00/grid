package com.futechsoft.admin.menu.mapper;

import java.util.List;

import com.futechsoft.admin.menu.vo.Menu;
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
@Mapper("menu.mapper.MenuMapper")
public interface MenuMapper {

	List<Menu> getMenuList(FtMap params) throws Exception;

	void insertMenu(FtMap params) throws Exception;
	
	void updateMenu(FtMap params) throws Exception;
	
	
	int getMenuCount(FtMap params) throws Exception;
	
	void deleteRoleMenu(FtMap params) throws Exception;
	void deleteMenu(FtMap params) throws Exception;
	
	void updateMenuOrd(FtMap params) throws Exception;
	

}
