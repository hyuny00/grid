package com.futechsoft.admin.auth.vo;

public class Role {

	private long roleSeq;
	private long upRoleSeq;
	private String roleCd;
	private String roleNm;
	private String upRoleNm;
	private String roleTypeCd;
	private String useYn;
	private int subRoleCnt;

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

	public String getRoleNm() {
		return roleNm;
	}

	public void setRoleNm(String roleNm) {
		this.roleNm = roleNm;
	}

	public String getUpRoleNm() {
		return upRoleNm;
	}

	public void setUpRoleNm(String upRoleNm) {
		this.upRoleNm = upRoleNm;
	}

	public String getRoleTypeCd() {
		return roleTypeCd;
	}

	public void setRoleTypeCd(String roleTypeCd) {
		this.roleTypeCd = roleTypeCd;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public int getSubRoleCnt() {
		return subRoleCnt;
	}

	public void setSubRoleCnt(int subRoleCnt) {
		this.subRoleCnt = subRoleCnt;
	}

}
