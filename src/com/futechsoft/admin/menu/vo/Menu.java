package com.futechsoft.admin.menu.vo;

/**
 * <PRE>
 *
 * </PRE>
 *
 * @author  futech
 * @version $Revision$
 */
public class Menu {

	private long menuSeq;
	private long upMenuSeq;
	private String menuNm;
	private String upMenuNm;
	private String menuUrl;
	private String menuTypeCd;
	private String menuOrd;
	private String etc;
	private String useYn;
	private String openYn;

	public String getOpenYn() {
		return openYn;
	}

	public void setOpenYn(String openYn) {
		this.openYn = openYn;
	}

	private long topMenuSeq;

	private int subMenuCnt;

	public long getMenuSeq() {
		return menuSeq;
	}

	public void setMenuSeq(long menuSeq) {
		this.menuSeq = menuSeq;
	}

	public long getUpMenuSeq() {
		return upMenuSeq;
	}

	public void setUpMenuSeq(long upMenuSeq) {
		this.upMenuSeq = upMenuSeq;
	}

	public String getMenuNm() {
		return menuNm;
	}

	public void setMenuNm(String menuNm) {
		this.menuNm = menuNm;
	}

	public String getUpMenuNm() {
		return upMenuNm;
	}

	public void setUpMenuNm(String upMenuNm) {
		this.upMenuNm = upMenuNm;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getMenuTypeCd() {
		return menuTypeCd;
	}

	public void setMenuTypeCd(String menuTypeCd) {
		this.menuTypeCd = menuTypeCd;
	}

	public String getMenuOrd() {
		return menuOrd;
	}

	public void setMenuOrd(String menuOrd) {
		this.menuOrd = menuOrd;
	}

	public String getEtc() {
		return etc;
	}

	public void setEtc(String etc) {
		this.etc = etc;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public int getSubMenuCnt() {
		return subMenuCnt;
	}

	public void setSubMenuCnt(int subMenuCnt) {
		this.subMenuCnt = subMenuCnt;
	}

	public long getTopMenuSeq() {
		return topMenuSeq;
	}

	public void setTopMenuSeq(long topMenuSeq) {
		this.topMenuSeq = topMenuSeq;
	}




}
