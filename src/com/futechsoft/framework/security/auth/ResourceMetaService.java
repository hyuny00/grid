package com.futechsoft.framework.security.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Component;

import com.futechsoft.admin.auth.vo.Path;
import com.futechsoft.admin.auth.vo.PathRole;
import com.futechsoft.framework.common.constant.RoleConstant;
import com.futechsoft.framework.security.event.SecurityMetaDataSourceEvent;
import com.futechsoft.framework.security.service.SecurityService;

@Component("framework.security.ResourceMetaService")
public class ResourceMetaService {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	SecurityService securityService;

	private LinkedHashMap<String, List<ConfigAttribute>> requestMap;

	private HashMap<String, String> pathAuthMap;

	@PostConstruct
	public void createSecurityMetaDataSource() throws Exception {

		List<Path> pathList = securityService.getAllPathList();
		requestMap = new LinkedHashMap<String, List<ConfigAttribute>>();

		pathAuthMap= new HashMap<String, String>();

		String auths="";
		for (Path path : pathList) {
			 auths="";

			List<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();

			List<PathRole> pathAuthList = securityService.getPathAuthList(path.getPathSeq());
			for (PathRole pathAuth : pathAuthList) {

				if(!pathAuth.getRoleCd().startsWith(RoleConstant.ROLE_PREFIX)){
					atts.add(new SecurityConfig(RoleConstant.ROLE_PREFIX + pathAuth.getRoleCd()));

					auths +="'"+RoleConstant.ROLE_PREFIX + pathAuth.getRoleCd()+"'";
				}else {
					atts.add(new SecurityConfig( pathAuth.getRoleCd()));

					auths +="'"+pathAuth.getRoleCd()+"'";
				}
				auths+=",";
			}

			requestMap.put(path.getPath(), atts);

			auths=StringUtils.removeEnd(auths, ",");

			pathAuthMap.put(path.getPath(), auths);
		}


		publisher.publishEvent(new SecurityMetaDataSourceEvent(this, requestMap, pathAuthMap));
	}
}
