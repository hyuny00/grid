package com.futechsoft.framework.security.event;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.menu.vo.Menu;

@Component("framework.security.auth.ResourceMenuEventListener")
public class ResourceMenuEventListener implements ApplicationListener<ResourceMenuEvent> {

	private Map<String, List<RoleMenu>> roleMenuMap;
	private Map<String, List<Menu>> menuListMap;

	public Map<String, List<RoleMenu>> roleMenuMap() {
		return roleMenuMap;
	}

	public Map<String, List<Menu>> getMenuListMap() {
		return menuListMap;
	}

	@Override
	public void onApplicationEvent(ResourceMenuEvent event) {
		this.roleMenuMap = event.getRoleMenuMap();
		this.menuListMap = event.getMenuListMap();
	}

}
