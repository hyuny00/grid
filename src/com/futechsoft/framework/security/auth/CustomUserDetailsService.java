package com.futechsoft.framework.security.auth;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.futechsoft.admin.user.vo.User;
import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.exception.BizException;
import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.security.service.SecurityService;
import com.futechsoft.framework.security.vo.CustomUserDetails;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.ConvertUtil;

public class CustomUserDetailsService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

	@Resource(name = "framework.security.service.SecurityService")
	SecurityService securityService;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		User user = null;
		try {
			user = securityService.getUserInfo(userId);
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			LOGGER.error(e.toString());
		}


		if (user == null) {
			throw new InternalAuthenticationServiceException(ErrorCode.BAD_CREDENTIALS.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsEnabled())) {
			throw new DisabledException(ErrorCode.DISABLED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsAccountNonExpired())) {
			throw new AccountExpiredException(ErrorCode.ACCOUNT_EXPIRED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsCredentialsNonExpired())) {
			throw new CredentialsExpiredException( ErrorCode.CREDENTIALS_EXPIRED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsAccountNonLocked())) {
			throw new LockedException( ErrorCode.LOCKED_USER.getMessage());
		}

		CustomUserDetails userDetails = new CustomUserDetails();
		ConvertUtil.copyProperties(user, userDetails);

		userDetails.setEnabled(CommonUtil.isTrue(user.getIsEnabled()));
		userDetails.setAccountNonExpired(CommonUtil.isTrue(user.getIsAccountNonExpired()));
		userDetails.setAccountNonLocked(CommonUtil.isTrue(user.getIsAccountNonLocked()));
		userDetails.setCredentialsNonExpired(CommonUtil.isTrue(user.getIsCredentialsNonExpired()));

		List<UserRole> userAuthList = null;
		try {
			userAuthList = securityService.getUserAuthList(userId);
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error(e.toString());
		}

		userDetails.setUserRoleList(userAuthList);

		return userDetails;
	}


	public UserDetails getUserInfoByDn(String userDn) throws UsernameNotFoundException {

		User user = null;
		try {
			user = securityService.getUserInfoByDn(userDn);
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			LOGGER.error(e.toString());
			throw new InternalAuthenticationServiceException(ErrorCode.USER_DUP_FOUND  .getMessage());
		}


		if (user == null) {
			throw new InternalAuthenticationServiceException(ErrorCode.USER_NOT_FOUND .getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsEnabled())) {
			throw new DisabledException(ErrorCode.DISABLED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsAccountNonExpired())) {
			throw new AccountExpiredException(ErrorCode.ACCOUNT_EXPIRED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsCredentialsNonExpired())) {
			throw new CredentialsExpiredException( ErrorCode.CREDENTIALS_EXPIRED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsAccountNonLocked())) {
			throw new LockedException( ErrorCode.LOCKED_USER.getMessage());
		}

		CustomUserDetails userDetails = new CustomUserDetails();
		ConvertUtil.copyProperties(user, userDetails);

		userDetails.setEnabled(CommonUtil.isTrue(user.getIsEnabled()));
		userDetails.setAccountNonExpired(CommonUtil.isTrue(user.getIsAccountNonExpired()));
		userDetails.setAccountNonLocked(CommonUtil.isTrue(user.getIsAccountNonLocked()));
		userDetails.setCredentialsNonExpired(CommonUtil.isTrue(user.getIsCredentialsNonExpired()));

		List<UserRole> userAuthList = null;
		try {
			userAuthList = securityService.getUserAuthList(user.getUserId());
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error(e.toString());
		}

		userDetails.setUserRoleList(userAuthList);

		return userDetails;
	}



	public UserDetails getUserInfoBySsoId(String ssoId) throws UsernameNotFoundException {

		User user = null;
		try {
			user = securityService.getUserInfoBySsoId(ssoId);
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
			LOGGER.error(e.toString());
			throw new InternalAuthenticationServiceException(ErrorCode.USER_DUP_FOUND  .getMessage());
		}


		if (user == null) {
			throw new InternalAuthenticationServiceException(ErrorCode.SSO_USER_NOT_FOUND .getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsEnabled())) {
			throw new DisabledException(ErrorCode.DISABLED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsAccountNonExpired())) {
			throw new AccountExpiredException(ErrorCode.ACCOUNT_EXPIRED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsCredentialsNonExpired())) {
			throw new CredentialsExpiredException( ErrorCode.CREDENTIALS_EXPIRED_USER.getMessage());
		}

		if (!CommonUtil.isTrue(user.getIsAccountNonLocked())) {
			throw new LockedException( ErrorCode.LOCKED_USER.getMessage());
		}

		CustomUserDetails userDetails = new CustomUserDetails();
		ConvertUtil.copyProperties(user, userDetails);

		userDetails.setEnabled(CommonUtil.isTrue(user.getIsEnabled()));
		userDetails.setAccountNonExpired(CommonUtil.isTrue(user.getIsAccountNonExpired()));
		userDetails.setAccountNonLocked(CommonUtil.isTrue(user.getIsAccountNonLocked()));
		userDetails.setCredentialsNonExpired(CommonUtil.isTrue(user.getIsCredentialsNonExpired()));

		List<UserRole> userAuthList = null;
		try {
			userAuthList = securityService.getUserAuthList(user.getUserId());
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error(e.toString());
		}

		userDetails.setUserRoleList(userAuthList);

		return userDetails;
	}

}
