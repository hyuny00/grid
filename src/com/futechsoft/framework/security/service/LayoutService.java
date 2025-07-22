package com.futechsoft.framework.security.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futechsoft.admin.auth.vo.RoleMenu;
import com.futechsoft.admin.menu.vo.Menu;
import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.common.constant.RoleConstant;
import com.futechsoft.framework.security.event.ResourceMenuEventListener;
import com.futechsoft.framework.security.vo.CustomUserDetails;
import com.futechsoft.framework.security.vo.MenuVO;
import com.futechsoft.framework.util.SecurityUtil;

@Service("framework.security.service.LayoutService")
public class LayoutService {

	@Autowired
	private ResourceMenuEventListener resourceMenuEventListener;
/*
	public List<AuthMenu> getAuthMenuList() {

		Map<String, List<AuthMenu>> menuMap = resourceMenuEventListener.authMenuMap();

		List<String> tempMenuList = new ArrayList<String>();

		List<UserAuth> userAuthList = getUserAuthList();

		List<AuthMenu> authMenuList = new ArrayList<AuthMenu>();
		for (UserAuth userAuth : userAuthList) {

			List<AuthMenu> tmpList = (List<AuthMenu>) menuMap.get(userAuth.getAuthCd());

			if (tmpList != null) {
				for (AuthMenu authMenu : tmpList) {

					if (!tempMenuList.contains(String.valueOf(authMenu.getMenuSeq() ))) {
						authMenuList.add(authMenu);
					}
					tempMenuList.add(String.valueOf(authMenu.getMenuSeq()));

				}
			}
		}

		Collections.sort(authMenuList, new Comparator<AuthMenu>() {

			public int compare(AuthMenu authMenu1, AuthMenu authMenu2) {

				String s1 = authMenu1.getUpMenuSeq() + authMenu1.getMenuOrd() + authMenu1.getMenuTypeCd();
				String s2 = authMenu2.getUpMenuSeq() + authMenu2.getMenuOrd() + authMenu2.getMenuTypeCd();

				if (Integer.parseInt(s1) < Integer.parseInt(s2)) {
					return -1;
				} else if (Integer.parseInt(s1) > Integer.parseInt(s2)) {
					return 1;
				} else {
					return 0;
				}

			}
		});

		return authMenuList;
	}

	public List<Long> getTopMenuSeqList() {

		Map<String, List<AuthMenu>> menuMap = resourceMenuEventListener.authMenuMap();

		List<Long> topMenuSeqList = new ArrayList<Long>();
		List<Long> tempTopMenuList = new ArrayList<Long>();

		List<UserAuth> userAuthList = getUserAuthList();

		for (UserAuth userAuth : userAuthList) {

			List<AuthMenu> tmpList = (List<AuthMenu>) menuMap.get(userAuth.getAuthCd());

			if (tmpList != null) {
				for (AuthMenu authMenu : tmpList) {
					if (!tempTopMenuList.contains(authMenu.getTopMenuSeq())) {
						topMenuSeqList.add(authMenu.getTopMenuSeq());
					}
					tempTopMenuList.add(authMenu.getTopMenuSeq());
				}
			}
		}

		return topMenuSeqList;
	}
*/
	
	public Map<String, List<Menu>> getMenuListMap() {
		return resourceMenuEventListener.getMenuListMap();
	}
	

	private List<UserRole> getUserAuthList() {
		CustomUserDetails userDetails = SecurityUtil.getPrincipal();
		List<UserRole> userAuthList = null;

		if (userDetails != null) {
			userAuthList = userDetails.getUserRoleList();
		} else {
			userAuthList = new ArrayList<UserRole>();
			UserRole userAuth = new UserRole();
			userAuth.setRoleCd(RoleConstant.ROLE_ANONYMOUS);
			userAuthList.add(userAuth);
		}
		return userAuthList;
	}

	

	
	 public List<MenuVO> getAuthMenuTree(Long selectedMenuSeq) {
	        Map<String, List<RoleMenu>> menuMap = resourceMenuEventListener.roleMenuMap();
	        List<UserRole> userAuthList = getUserAuthList();

	        return buildAuthMenuTree(menuMap, userAuthList, selectedMenuSeq);
	    }

	
	
	public List<MenuVO> buildAuthMenuTree(Map<String, List<RoleMenu>> menuMap, List<UserRole> userAuthList, Long selectedMenuSeq) {
	    Map<Long, MenuVO> menuVOMap = new HashMap<>();
	    List<MenuVO> rootMenus = new ArrayList<>();
	    Set<Long> authMenuSeqs = new HashSet<>();

	    // 현재 사용자의 권한별 메뉴 가져오기
	    for (UserRole userAuth : userAuthList) {
	        List<RoleMenu> authMenus = menuMap.get(userAuth.getRoleCd());
	        if (authMenus != null) {
	            for (RoleMenu authMenu : authMenus) {
	                authMenuSeqs.add(authMenu.getMenuSeq()); // 권한 있는 메뉴 ID 저장
	            }
	        }
	    }

	    // MenuVO 객체 생성 (권한 있는 메뉴만)
	    for (UserRole userAuth : userAuthList) {
	        List<RoleMenu> authMenus = menuMap.get(userAuth.getRoleCd());
	        if (authMenus != null) {
	            for (RoleMenu authMenu : authMenus) {
	                if (!menuVOMap.containsKey(authMenu.getMenuSeq())) {

	                	MenuVO vo = new MenuVO(authMenu, Objects.equals(authMenu.getMenuSeq(), selectedMenuSeq));

	                    menuVOMap.put(vo.getMenuSeq(), vo);
	                }
	            }
	        }
	    }

	    // 부모-자식 관계 설정
	    for (MenuVO menu : menuVOMap.values()) {
	        if (menu.getUpMenuSeq() == null || menu.getUpMenuSeq() == 0) {
	            rootMenus.add(menu); // 최상위 메뉴
	        } else {
	            MenuVO parent = menuVOMap.get(menu.getUpMenuSeq());
	            if (parent != null) {
	                parent.getChildren().add(menu);
	            }
	        }
	    }

	    // 정렬 (menuOrd 기준)
	    rootMenus.sort(Comparator.comparing(MenuVO::getMenuOrd));
	    for (MenuVO menu : menuVOMap.values()) {
	        menu.getChildren().sort(Comparator.comparing(MenuVO::getMenuOrd));
	    }

	    return rootMenus;
	}

}
