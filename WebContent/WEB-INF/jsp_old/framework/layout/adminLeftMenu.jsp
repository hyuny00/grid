<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf"%>

<script>
    function logout() {
        document.logoutForm.submit();
    }

    function updateUserForm() {
        document.userDetailForm.action="/admin/user/detailUser";
        document.userDetailForm.submit();
    }
</script>

<aside class="left-side-menu">
    <div class="user-info">
        <div class="user-info-profile">
            <a class="avatar" href="javascript:void(0);">
                <i><span style="display:none;">그림</span></i>
            </a>
            <sec:authorize access="isAuthenticated()">
                <span class="name"><span>${user.userNm}</span>님<br>환영합니다</span>
            </sec:authorize>
        </div>
        <div class="user-info-option">
            <a class="myinfo" href="javascript:updateUserForm();">
                개인정보관리<i></i>
            </a>

            <sec:authorize access="isAuthenticated()">
                <a class="logout" href="javascript:logout();">
                    로그아웃<i></i>
                </a>
            </sec:authorize>
        </div>
    </div>

    <!-- 왼쪽 메뉴 표시 영역 -->
    <ul class="left-side-list" style="display: block;">
        <!-- 메뉴는 JavaScript에서 동적으로 생성됩니다 -->
    </ul>
</aside>

<form id="logoutForm" name="logoutForm" action="${basePath}/logout" method="POST">
    <input type="hidden" name="${ _csrf.parameterName }" value="${ _csrf.token }">
</form>

<form id="userDetailForm" name="userDetailForm" method="POST">
    <input type="hidden" name="${ _csrf.parameterName }" value="${ _csrf.token }">
    <input type="hidden" name="userNo" value="${user.userNo}">
</form>

<script>
// 페이지 로드 시 왼쪽 메뉴 로드
$(document).ready(function() {
    const params = getUrlParams();
    
    if (params.topMenuSeq) {
        loadMenuData(function(data) {
            renderLeftMenu(data, params.topMenuSeq);
        });
    }
});

// 왼쪽 메뉴 렌더링 함수
function renderLeftMenu(menuData, topMenuSeq) {
    const $leftMenuUl = $('.left-side-list');
    $leftMenuUl.empty();
    
    // URL 파라미터에서
    const params = getUrlParams();
    
    // 선택된 탑 메뉴 찾기
    const selectedTopMenu = findMenuById(menuData, topMenuSeq);
    
    if (selectedTopMenu) {
        // 탑 메뉴 이름 표시
        $leftMenuUl.append($('<li>').addClass('tit').text(selectedTopMenu.menuNm));
        
        // 하위 메뉴가 있는 경우 처리
        if (selectedTopMenu.children && selectedTopMenu.children.length > 0) {
            // 메뉴 순서(menuOrd)에 따라 정렬
            const sortedSubMenus = [...selectedTopMenu.children].sort((a, b) => a.menuOrd - b.menuOrd);
            
            // 하위 메뉴 생성 (menuTypeCd가 020인 항목)
            sortedSubMenus.forEach(subMenu => {
                if (subMenu.menuTypeCd === '020' || subMenu.menuTypeCd === '030' ) {
                    const isActive = (params.upMenuSeq == subMenu.menuSeq) ? 'active' : '';
                    const $li = $('<li>').addClass(isActive).attr('data-menu-seq', subMenu.menuSeq);
                    
                    // 하위 메뉴 링크 생성
                    let $a;
                    if (subMenu.children && subMenu.children.length > 0) {
                        // 더 하위 메뉴가 있는 경우 (030)
                        $a = $('<a>')
                            .attr('href', 'javascript:void(0);')
                            .attr('onclick', `toggleLeftSubmenu(this)`)
                            .html(subMenu.menuNm + '<i class="fas fa-chevron-right"></i>');
                    } else if (subMenu.menuUrl) {
                        // 링크가 있는 경우 (바로 이동 가능한 020 메뉴)
                        $a = $('<a>')
                            .attr('href', buildMenuUrl(subMenu, topMenuSeq))
                            .text(subMenu.menuNm);
                    } else {
                        // 링크가 없는 경우 (클릭 불가능)
                        $a = $('<a>')
                            .attr('href', 'javascript:void(0);')
                            .text(subMenu.menuNm);
                    }
                    
                    $li.append($a);
                    
                    // 하위 메뉴가 있는 경우 (030)
                    if (subMenu.children && subMenu.children.length > 0) {
                        const isSubMenuVisible = (params.upMenuSeq == subMenu.menuSeq);
                        const $subUl = $('<ul>').css('display', isSubMenuVisible ? 'block' : 'none');
                        
                        // 메뉴 순서(menuOrd)에 따라 정렬
                        const sortedLeafMenus = [...subMenu.children].sort((a, b) => a.menuOrd - b.menuOrd);
                        
                        // 가장 하위 메뉴 생성 (menuTypeCd가 030인 항목)
                        sortedLeafMenus.forEach(leafMenu => {
                            if (leafMenu.menuTypeCd === '030') {
                                const isLeafActive = (params.menuSeq == leafMenu.menuSeq) ? 'active' : '';
                                const $subLi = $('<li>').addClass(isLeafActive).attr('data-menu-seq', leafMenu.menuSeq);
                                
                                const $subA = $('<a>')
                                    .attr('href', buildMenuUrl(leafMenu, topMenuSeq, subMenu.menuSeq))
                                    .text(leafMenu.menuNm);
                                
                                $subLi.append($subA);
                                $subUl.append($subLi);
                            }
                        });
                        
                        $li.append($subUl);
                    }
                    
                    $leftMenuUl.append($li);
                }
            });
        }
    }
    
    // 메뉴 상태 초기화 (활성화된 메뉴 표시)
    initMenuState();
}
</script>