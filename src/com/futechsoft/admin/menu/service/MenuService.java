package com.futechsoft.admin.menu.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futechsoft.admin.menu.mapper.MenuMapper;
import com.futechsoft.admin.menu.vo.Menu;
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
@Service("menu.service.MenuService")
public class MenuService {


	private static final Logger LOGGER = LoggerFactory.getLogger(MenuService.class);


	@Resource(name = "menu.mapper.MenuMapper")
	private MenuMapper menuMapper;

	public List<Menu> getList(FtMap params) throws Exception {

		return menuMapper.getMenuList(params);
	}

	public void save(FtMap params) throws Exception {

		params.put("userNo", SecurityUtil.getUserNo());
		if (params.getString("mode").equals("new")) {
			params.put("menuOrd", 99);
			menuMapper.insertMenu(params);
		} else {
			menuMapper.updateMenu(params);
		}

	}
	
	@Transactional
	public void saveMenuOrd(String[] menuSlnos) throws Exception {

		FtMap params = new FtMap();
		for (int i = 0; i < menuSlnos.length; i++) {
			params.put("menuSeq", menuSlnos[i]);
			params.put("menuOrd", i);
			menuMapper.updateMenuOrd(params);
		}

	}

	@Transactional
	public void delete(FtMap params) throws Exception {

		menuMapper.deleteRoleMenu(params);
		menuMapper.deleteMenu(params);

	}

	public boolean hasChildren(FtMap params) throws Exception {

		params.put("up_menu_seq", params.getString("menu_seq"));
		int count = menuMapper.getMenuCount(params);
				
		if (count == 0)
			return false;
		else
			return true;

	}
}
