/**
 * 메뉴 관리 공통 JavaScript 파일
 * common-menu.js
 */

// 메뉴 데이터 전역 변수
let menuData = null;

// 메뉴 데이터 로드
function loadMenuData(callback) {
    if (menuData !== null) {
        // 이미 로드된 데이터가 있으면 재사용
        if (typeof callback === 'function') {
            callback(menuData);
        }
        return;
    }
    
    $.ajax({
        url: '/framework/layout/authMenuTree',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            menuData = data;
            
            if (typeof callback === 'function') {
                callback(data);
            }
        },
        error: function(xhr, status, error) {
            console.error('메뉴 로드 실패:', error);
        }
    });
}

// 현재 URL에서 파라미터 추출하는 함수
function getUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    return {
        topMenuSeq: urlParams.get('topMenuSeq'),
        menuSeq: urlParams.get('menuSeq'),
        upMenuSeq: urlParams.get('upMenuSeq')
    };
}

// 메뉴 ID로 메뉴 객체 찾기 (재귀)
function findMenuById(menuData, menuSeq) {
    if (!menuData || !menuSeq) return null;
    
    for (const menu of menuData) {
        if (menu.menuSeq == menuSeq) {
            return menu;
        }
        
        if (menu.children && menu.children.length > 0) {
            const found = findMenuById(menu.children, menuSeq);
            if (found) {
                return found;
            }
        }
    }
    
    return null;
}

// 첫 번째 네비게이션 가능한 서브메뉴 찾기 (재귀)
function findFirstNavigableSubmenu(menuList) {
    if (!menuList || menuList.length === 0) return null;
    
    for (const menu of menuList) {
        if (menu.menuUrl) {
            return menu;
        }
        
        if (menu.children && menu.children.length > 0) {
            const found = findFirstNavigableSubmenu(menu.children);
            if (found) {
                return found;
            }
        }
    }
    
    return null;
}

// 메뉴 URL 생성 함수
function buildMenuUrl(menu, topMenuSeq, upMenuSeq) {
    if (!menu || !menu.menuUrl) return 'javascript:void(0);';
    
    let url = menu.menuUrl;
    url += (url.includes('?') ? '&' : '?') + 'menuSeq=' + menu.menuSeq;
    
    if (topMenuSeq) {
        url += '&topMenuSeq=' + topMenuSeq;
    } else if (menu.menuTypeCd === '010') {
        url += '&topMenuSeq=' + menu.menuSeq;
    }
    
    if (upMenuSeq) {
        url += '&upMenuSeq=' + upMenuSeq;
    }
    
    return url;
}

// 탑 메뉴 클릭 처리 함수
function handleTopMenuClick(menuSeq) {
    if (!menuData) {
        loadMenuData(function(data) {
            processTopMenuNavigation(data, menuSeq);
        });
    } else {
        processTopMenuNavigation(menuData, menuSeq);
    }
}

// 탑 메뉴 네비게이션 처리 함수
function processTopMenuNavigation(data, menuSeq) {
    const menuItem = findMenuById(data, menuSeq);
    
    if (menuItem) {
        if (menuItem.menuUrl) {
            // URL이 있는 경우 직접 이동
            window.location.href = buildMenuUrl(menuItem, menuSeq);
        } else if (menuItem.children && menuItem.children.length > 0) {
            // URL이 없고 하위 메뉴가 있는 경우, 첫 번째 접근 가능한 하위 메뉴로 이동
            const childWithUrl = findFirstNavigableSubmenu(menuItem.children);
            
            if (childWithUrl) {
                const upMenuSeq = childWithUrl.upMenuSeq || menuSeq;
                window.location.href = buildMenuUrl(childWithUrl, menuSeq, upMenuSeq);
            }
        }
    }
}

// 왼쪽 메뉴 토글 함수
function toggleLeftSubmenu(element) {
    const $submenu = $(element).siblings('ul');
    $submenu.toggle();
    $(element).parent().toggleClass('active');
}

// 페이지 로드 시 메뉴 상태 설정 함수
function initMenuState() {
    const params = getUrlParams();
    
    if (params.topMenuSeq) {
        // 탑 메뉴 활성화
        $('.gnb ul li').removeClass('active');
        $(`.gnb ul li[data-menu-seq="${params.topMenuSeq}"]`).addClass('active');
        
        // 왼쪽 메뉴 활성화
        if (params.upMenuSeq) {
            $(`.left-side-list > li[data-menu-seq="${params.upMenuSeq}"]`).addClass('active');
            $(`.left-side-list > li[data-menu-seq="${params.upMenuSeq}"] > ul`).show();
            
            if (params.menuSeq) {
                $(`.left-side-list > li > ul > li[data-menu-seq="${params.menuSeq}"]`).addClass('active');
            }
        }
    }
}