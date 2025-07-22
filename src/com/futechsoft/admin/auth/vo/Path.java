package com.futechsoft.admin.auth.vo;

public class Path {

	private int pathSeq;
	private int upPathSeq;
	private String path;
	private String pathNm;
	private int pathOrd;
	private int subCnt;
	private String upPath;

	public int getPathSeq() {
		return pathSeq;
	}
	public void setPathSeq(int pathSeq) {
		this.pathSeq = pathSeq;
	}
	public int getUpPathSeq() {
		return upPathSeq;
	}
	public void setUpPathSeq(int upPathSeq) {
		this.upPathSeq = upPathSeq;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPathNm() {
		return pathNm;
	}
	public void setPathNm(String pathNm) {
		this.pathNm = pathNm;
	}
	public int getPathOrd() {
		return pathOrd;
	}
	public void setPathOrd(int pathOrd) {
		this.pathOrd = pathOrd;
	}
	public int getSubCnt() {
		return subCnt;
	}
	public void setSubCnt(int subCnt) {
		this.subCnt = subCnt;
	}
	public String getUpPath() {
		return upPath;
	}
	public void setUpPath(String upPath) {
		this.upPath = upPath;
	}


}
