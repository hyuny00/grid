<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf"%>
<header class="header" id="header">
    <nav class="gnb">
        <h1 class="logo">
            <a href="/main">
                <img alt="" src="${basePath}/img/logo.png">ODA통합정보포털
            </a>
        </h1>
        <ul>
            <!-- 메뉴는 JavaScript에서 동적으로 생성됩니다 -->
        </ul>
        <form id="searechForm" name="searechForm" action="${basePath}/searech" method="POST">
            <input type="hidden" name="${ _csrf.parameterName }" value="${ _csrf.token }">
            <div class="search">
                <input class="input" type="text" placeholder="검색 조건 입력">
                <button type="submit">검색</button>
            </div>
        </form>
    </nav>
</header>


<script>
// 페이지 로드 시 탑 메뉴 렌더링
$(document).ready(function() {
    // 공통 메뉴 데이터 로드
    loadMenuData(function(data) {
        renderTopMenu(data);
        // 이벤트 연결을 jQuery로 처리
        attachTopMenuEvents();
    });
});

// 탑 메뉴 렌더링 함수
function renderTopMenu(menuData) {
    const $topMenuUl = $('.gnb ul');
    $topMenuUl.empty();
    
    // URL 파라미터에서 현재 선택된 탑 메뉴 가져오기
    const params = getUrlParams();
    const currentTopMenuSeq = params.topMenuSeq;
    
    // 탑 메뉴(menuTypeCd가 010) 필터링
    const topMenus = menuData.filter(menu => menu.menuTypeCd === '010');
    
    // 메뉴 순서(menuOrd)에 따라 정렬
    topMenus.sort((a, b) => a.menuOrd - b.menuOrd);
    
    // 탑 메뉴 생성
    topMenus.forEach(menu => {
        const isActive = currentTopMenuSeq == menu.menuSeq ? 'active' : '';
        
        const $li = $('<li>')
            .addClass(isActive)
            .attr('data-menu-seq', menu.menuSeq);
            
        const $a = $('<a>')
            .attr('title', menu.menuNm)
            .attr('href', 'javascript:void(0);')
            .attr('data-menu-seq', menu.menuSeq) // 메뉴 시퀀스를 데이터 속성으로 저장
            .text(menu.menuNm);
        
        $li.append($a);
        $topMenuUl.append($li);
    });
    
    // 메뉴 상태 초기화 (활성화된 메뉴 표시)
    initMenuState();
}

// 탑 메뉴 클릭 이벤트 연결 함수
function attachTopMenuEvents() {
    // 클릭 이벤트를 jQuery 방식으로 연결
    $('.gnb ul li a').on('click', function(e) {
        e.preventDefault();
        const menuSeq = $(this).data('menu-seq');
        handleTopMenuClick(menuSeq);
    });
}
</script>