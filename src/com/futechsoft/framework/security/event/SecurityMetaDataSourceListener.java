package com.futechsoft.framework.security.event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Component;

@Component("framework.security.SecurityMetaDataSourceListener")
public class SecurityMetaDataSourceListener implements ApplicationListener<SecurityMetaDataSourceEvent> {

	private LinkedHashMap<String, List<ConfigAttribute>> requestMap;

	private HashMap<String, String> pathRoleMap;

	public LinkedHashMap<String, List<ConfigAttribute>> getRequestMap() {
		return requestMap;
	}

	public HashMap<String, String> getPathRoleMap() {
		return pathRoleMap;
	}


	@Override
	public void onApplicationEvent(SecurityMetaDataSourceEvent event) {
		this.requestMap = event.getRequestMap();
		this.pathRoleMap = event.getPathRoleMap();
	}

}
