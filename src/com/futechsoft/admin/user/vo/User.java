package com.futechsoft.admin.user.vo;

import org.apache.commons.lang.StringUtils;

public class User {

	private String userNo;
	private String userId;
	private String userNm;
	private String userPwd;
	private String userEmail;
	private String telNo;
	private String mobile;
	private int loginFailCnt;
	private String pwdChngDttm;
	private String rgstpNo;
	private String rgstDttm;
	private String updtpNo;
	private String updtDttm;

	private String isEnabled = "Y";
	private String isAccountNonExpired = "Y";
	private String isCredentialsNonExpired = "Y";
	private String isAccountNonLocked = "Y";
	private String acctLockedYn = "N";

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getUserPwd() {
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

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getLoginFailCnt() {
		return loginFailCnt;
	}

	public void setLoginFailCnt(int loginFailCnt) {
		this.loginFailCnt = loginFailCnt;
	}

	public String getPwdChngDttm() {
		return pwdChngDttm;
	}

	public void setPwdChngDttm(String pwdChngDttm) {
		this.pwdChngDttm = pwdChngDttm;
	}

	public String getRgstpNo() {
		return rgstpNo;
	}

	public void setRgstpNo(String rgstpNo) {
		this.rgstpNo = rgstpNo;
	}

	public String getRgstDttm() {
		return rgstDttm;
	}

	public void setRgstDttm(String rgstDttm) {
		this.rgstDttm = rgstDttm;
	}

	public String getUpdtpNo() {
		return updtpNo;
	}

	public void setUpdtpNo(String updtpNo) {
		this.updtpNo = updtpNo;
	}

	public String getUpdtDttm() {
		return updtDttm;
	}

	public void setUpdtDttm(String updtDttm) {
		this.updtDttm = updtDttm;
	}

	public String getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getIsAccountNonExpired() {
		return isAccountNonExpired;
	}

	public void setIsAccountNonExpired(String isAccountNonExpired) {
		this.isAccountNonExpired = isAccountNonExpired;
	}

	public String getIsCredentialsNonExpired() {
		return isCredentialsNonExpired;
	}

	public void setIsCredentialsNonExpired(String isCredentialsNonExpired) {
		this.isCredentialsNonExpired = isCredentialsNonExpired;
	}

	public String getIsAccountNonLocked() {
		return isAccountNonLocked;
	}

	public void setIsAccountNonLocked(String isAccountNonLocked) {
		this.isAccountNonLocked = isAccountNonLocked;

	}

	public String getAcctLockedYn() {
		return acctLockedYn;
	}

	public void setAcctLockedYn(String acctLockedYn) {
		setIsAccountNonLocked(StringUtils.defaultString(acctLockedYn).equals("Y") ? "N" : "Y");
		this.acctLockedYn = acctLockedYn;
	}

}
