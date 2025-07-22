
package com.futechsoft.framework.security.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.futechsoft.admin.auth.mapper.RoleMapper;
import com.futechsoft.admin.auth.vo.Role;
import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.menu.vo.Menu;
import com.futechsoft.framework.security.mapper.SecurityMapper;
import com.futechsoft.framework.exception.BizException;
import com.futechsoft.framework.security.event.ResourceMenuEvent;

/**
 * <PRE>
 *
 * </PRE>
 *
 * @author futech
 * @version $Revision$
 */
@Component("framework.menu.service.ResourceMenuService")
public class ResourceMenuService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMenuService.class);

	@Autowired
	private ApplicationEventPublisher publisher;

	@Resource(name = "framework.security.mapper.SecurityMapper")
	private SecurityMapper securityMapper;

	//@Resource(name = "admin.logging.menu.service.MenuLogService")
//	private MenuLogService menuLogService;


	@Resource(name = "auth.mapper.RoleMapper")
	private RoleMapper roleMapper;

	Map<String, List<RoleMenu>> roleMenuMap = null;
	Map<String, List<Menu>> menuListMap = null;

	@PostConstruct
	public void init() throws Exception {

		roleMenuMap = new HashMap<String, List<RoleMenu>>();

		List<Role> list = roleMapper.getRoleCdList();
		for (Role auth : list) {
			List<RoleMenu> menuList = securityMapper.getRoleMenu(auth.getRoleSeq());
			roleMenuMap.put(auth.getRoleCd().toUpperCase(), menuList);
		}

		menuListMap = new HashMap<String, List<Menu>>();
		List<Menu> topMenuList = securityMapper.getTopMenuList();
		menuListMap.put("topMenuList", topMenuList);

		List<Menu> subMenuList = null;
		for (Menu topMenu : topMenuList) {
			subMenuList = securityMapper.getSubMenuList(topMenu.getMenuSeq());
			menuListMap.put(String.valueOf(topMenu.getMenuSeq()), subMenuList);
		}

		publisher.publishEvent(new ResourceMenuEvent(this, roleMenuMap, menuListMap));
	}

}



