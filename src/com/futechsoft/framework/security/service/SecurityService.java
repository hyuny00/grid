package com.futechsoft.framework.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futechsoft.admin.auth.vo.Path;
import com.futechsoft.admin.auth.vo.PathRole;
import com.futechsoft.admin.user.vo.User;
import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.security.mapper.SecurityMapper;

@Service("framework.security.service.SecurityService")
public class SecurityService {

	@Autowired
	private SecurityMapper securityMapper;

	public User getUserInfo(String userId) throws Exception {
		return securityMapper.getUserInfo(userId);
	}

	public List<UserRole> getUserAuthList(String userId) throws Exception {
		return securityMapper.getUserRoleList(userId);
	}

	public void resetFailCnt(String userId) throws Exception {
		securityMapper.resetFailCnt(userId);
	}

	public void plusFailCnt(String userId) throws Exception {
		securityMapper.plusFailCnt(userId);
	}

	public void disabledUser(String userId) throws Exception {
		securityMapper.disabledUser(userId);
	}

	public List<PathRole> getPathAuthList(long pathSeq) throws Exception {
		return securityMapper.getPathRoleList(pathSeq);
	}

	public List<Path> getAllPathList() throws Exception {
		return securityMapper.getAllPathList();
	}


	public User getUserInfoByDn(String userDn) throws Exception {
		return securityMapper.getUserInfoByDn(userDn);
	}


	public User getUserInfoBySsoId(String ssoId) throws Exception {
		return securityMapper.getUserInfoBySsoId(ssoId);
	}
}
