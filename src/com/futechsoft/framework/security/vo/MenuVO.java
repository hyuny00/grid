package com.futechsoft.framework.security.vo;

import java.util.ArrayList;
import java.util.List;

import com.futechsoft.admin.auth.vo.RoleMenu;



public class MenuVO {
    private Long menuSeq;
    private Long upMenuSeq;
    private String menuNm;
    private String menuUrl;
    private String menuTypeCd;
    private int menuOrd;
    private boolean isSelected;
    private List<MenuVO> children = new ArrayList<>();

    public MenuVO(RoleMenu menu, boolean isSelected) {
        this.menuSeq = menu.getMenuSeq();
        this.upMenuSeq = menu.getUpMenuSeq();
        this.menuNm = menu.getMenuNm();
        this.menuUrl = menu.getMenuUrl();
        this.menuTypeCd = menu.getMenuTypeCd();
        this.menuOrd = menu.getMenuOrd();
        this.isSelected = isSelected;
    }

	public Long getMenuSeq() {
		return menuSeq;
	}

	public void setMenuSeq(Long menuSeq) {
		this.menuSeq = menuSeq;
	}

	public Long getUpMenuSeq() {
		return upMenuSeq;
	}

	public void setUpMenuSeq(Long upMenuSeq) {
		this.upMenuSeq = upMenuSeq;
	}

	public String getMenuNm() {
		return menuNm;
	}

	public void setMenuNm(String menuNm) {
		this.menuNm = menuNm;
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

	public int getMenuOrd() {
		return menuOrd;
	}

	public void setMenuOrd(int menuOrd) {
		this.menuOrd = menuOrd;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public List<MenuVO> getChildren() {
		return children;
	}

	public void setChildren(List<MenuVO> children) {
		this.children = children;
	}

   
}
