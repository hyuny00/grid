package com.futechsoft.framework.security.event;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEvent;

import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.menu.vo.Menu;

public class ResourceMenuEvent extends ApplicationEvent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, List<RoleMenu>> roleMenuMap;
	private Map<String, List<Menu>> menuListMap;

	public ResourceMenuEvent(Object source, final Map<String, List<RoleMenu>> roleMenuMap, Map<String, List<Menu>> menuListMap) {
		super(source);
		this.roleMenuMap = roleMenuMap;
		this.menuListMap = menuListMap;
	}

	public Map<String, List<RoleMenu>> getRoleMenuMap() {
		return roleMenuMap;
	}

	public void setRoleMenuMap(Map<String, List<RoleMenu>> roleMenuMap) {
		this.roleMenuMap = roleMenuMap;
	}

	public Map<String, List<Menu>> getMenuListMap() {
		return menuListMap;
	}

	public void setMenuListMap(Map<String, List<Menu>> menuListMap) {
		this.menuListMap = menuListMap;
	}

}
