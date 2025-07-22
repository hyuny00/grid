package com.futechsoft.admin.auth.vo;

public class PathRole {

	private long pathRoleSeq;
	private long pathSeq;
	private long roleSeq;
	private long upRoleSeq;
	private String roleCd;
	private String roleTypeCd;
	private String  roleNm;
	private String path;

	private String selectYn;

	public long getPathRoleSeq() {
		return pathRoleSeq;
	}

	public void setPathRoleSeq(long pathRoleSeq) {
		this.pathRoleSeq = pathRoleSeq;
	}

	public long getPathSeq() {
		return pathSeq;
	}

	public void setPathSeq(long pathSeq) {
		this.pathSeq = pathSeq;
	}

	public long getRoleSeq() {
		return roleSeq;
	}

	public void setRoleSeq(long roleSeq) {
		this.roleSeq = roleSeq;
	}

	public long getUpRoleSeq() {
		return upRoleSeq;
	}

	public void setUpRoleSeq(long upRoleSeq) {
		this.upRoleSeq = upRoleSeq;
	}

	public String getRoleCd() {
		return roleCd;
	}

	public void setRoleCd(String roleCd) {
		this.roleCd = roleCd;
	}

	public String getRoleTypeCd() {
		return roleTypeCd;
	}

	public void setRoleTypeCd(String roleTypeCd) {
		this.roleTypeCd = roleTypeCd;
	}

	public String getRoleNm() {
		return roleNm;
	}

	public void setRoleNm(String roleNm) {
		this.roleNm = roleNm;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSelectYn() {
		return selectYn;
	}

	public void setSelectYn(String selectYn) {
		this.selectYn = selectYn;
	}


}
