package com.futechsoft.framework.security.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;

import com.futechsoft.framework.security.event.SecurityMetaDataSourceListener;

public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource, InitializingBean {

	private LinkedHashMap<String, List<ConfigAttribute>> requestMap;

	@Autowired
	ResourceMetaService resourceMetaService;

	@Autowired
	SecurityMetaDataSourceListener securityMetaDataSourceListener;

	AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {

		this.requestMap = securityMetaDataSourceListener.getRequestMap();

		if (requestMap == null)
			return null;
		String url = ((FilterInvocation) o).getRequestUrl();
		Iterator<String> ite = requestMap.keySet().iterator();

		Collection<ConfigAttribute> result = null;
		while (ite.hasNext()) {
			String resURL = ite.next();
			
			if (pathMatcher.match(resURL, url)) {
				result = requestMap.get(resURL);
				break;
			}
		}

		
		
		if (result == null || result.size() == 0) {
			result = new ArrayList<ConfigAttribute>();
			result.add(new SecurityConfig("ROLE_ADMIN"));
		}

		return result;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return FilterInvocation.class.isAssignableFrom(aClass);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		resourceMetaService.createSecurityMetaDataSource();
	}
}
