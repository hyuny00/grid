package com.futechsoft.framework.security.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.common.constant.RoleConstant;

public class CustomUserDetails implements UserDetails {

	public CustomUserDetails() {
	}

	private static final long serialVersionUID = 1L;

	private String userNo;
	private String userId;
	private String userNm;
	
	@JsonIgnore
	private String userPwd;
	
	private String userEmail;
	private String miniCd;
	private String dptCd;
	
	@JsonIgnore
	private int loginFailCnt;

	public int getLoginFailCnt() {
		return loginFailCnt;
	}

	public void setLoginFailCnt(int loginFailCnt) {
		this.loginFailCnt = loginFailCnt;
	}

	@JsonIgnore
	private boolean isEnabled;
	
	@JsonIgnore
	private boolean isAccountNonExpired;
	
	@JsonIgnore
	private boolean isAccountNonLocked;
	
	@JsonIgnore
	private boolean isCredentialsNonExpired;

	private List<UserRole> userRoleList;

	public String getUserNo(){
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUsername() {
		return userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	@JsonIgnore
	public String getPassword() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	@JsonIgnore
	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@JsonIgnore
	public boolean isAccountNonExpired() {
		return isAccountNonExpired;
	}

	public void setAccountNonExpired(boolean isAccountNonExpired) {
		this.isAccountNonExpired = isAccountNonExpired;
	}

	@JsonIgnore
	public boolean isAccountNonLocked() {
		return isAccountNonLocked;
	}

	public void setAccountNonLocked(boolean isAccountNonLocked) {
		this.isAccountNonLocked = isAccountNonLocked;
	}

	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean isCredentialsNonExpired) {
		this.isCredentialsNonExpired = isCredentialsNonExpired;
	}

	public void setUserRoleList(List<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}
	
	@JsonIgnore
	public List<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public Collection<GrantedAuthority> getAuthorities() {

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();


		for (UserRole userRole : userRoleList) {

			if(userRole.getRoleCd()!=null) {
				if(!userRole.getRoleCd().startsWith((RoleConstant.ROLE_PREFIX))){
					authorities.add(new SimpleGrantedAuthority(RoleConstant.ROLE_PREFIX + userRole.getRoleCd()));
				}else {
					authorities.add(new SimpleGrantedAuthority(userRole.getRoleCd()));
				}
			}
		}
/*
		if(authorities.size()==0) {
			authorities.add(new SimpleGrantedAuthority(AuthConstant.ROLE_ANONYMOUS));
		}
*/
		return authorities;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CustomUserDetails) {
			return userId.equals(((CustomUserDetails) obj).getUsername());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return userId != null ? userId.hashCode() : 0;
	}

	public String getDptCd() {
		return dptCd;
	}

	public void setDptCd(String dptCd) {
		this.dptCd = dptCd;
	}

	public String getMiniCd() {
		return miniCd;
	}

	public void setMiniCd(String miniCd) {
		this.miniCd = miniCd;
	}

	

}
