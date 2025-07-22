<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<style>


.tree-row { 
    display: none; 
    background: #f9f9f9; 
}
.tree-row.show {
    display: table-row;
}
td[contenteditable="true"] { 
    background-color: #fff8dc; 
    cursor: text;
}
.date-input { 
    width: 120px; 
}
.tree-indent {
    padding-left: 20px;
}
.tree-toggle {
    cursor: pointer;
    margin-right: 8px;
    display: inline-block;
    width: 16px;
    text-align: center;
    color: #007bff;
    font-weight: bold;
}
.tree-toggle:hover {
    color: #0056b3;
}
.tree-toggle.no-children {
    color: transparent;
    cursor: default;
}



.tree-row.hide {
    display: none;
}

.tree-row.show {
    display: table-row;
}

.context-menu {
    position: absolute;
    background: white;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    z-index: 10000;
    min-width: 150px;
    display: none;
}

.context-menu-item {
    padding: 8px 12px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
}

.context-menu-item:hover:not(.disabled) {
    background-color: #f0f0f0;
}

.context-menu-item.disabled {
    color: #ccc;
    cursor: not-allowed;
}

.context-menu-item i {
    width: 16px;
    font-size: 14px;
}

.context-menu-item .shortcut {
    margin-left: auto;
    font-size: 11px;
    color: #888;
}

.context-menu-separator {
    height: 1px;
    background-color: #eee;
    margin: 4px 0;
}

.context-selected {
    background-color: #e6f3ff !important;
}



.pagination .page-link {
    color: #007bff;
}

.pagination .page-item.disabled .page-link {
    color: #6c757d;
    cursor: not-allowed;
}

.pagination .page-item.active .page-link {
    background-color: #007bff;
    border-color: #007bff;
}

.page-info {
    font-size: 0.875rem;
    color: #6c757d;
}

.grid-container {
    margin-bottom: 30px;
    border: 1px solid #ddd;
    padding: 15px;
    border-radius: 5px;
}

.pagination-btn {
    display: inline-block;
    margin: 0 4px;
    padding: 4px 10px;
    border: 1px solid #ccc;
    background-color: #f9f9f9;
    color: #333;
    cursor: pointer;
    user-select: none;
    border-radius: 3px;
    font-size: 14px;
}

.pagination-btn:hover:not(.disabled):not(.active) {
    background-color: #eee;
}

.pagination-btn.active {
    background-color: #007bff;
    color: white;
    font-weight: bold;
}

.pagination-btn.disabled {
    color: #aaa;
    cursor: default;
    pointer-events: none;
    background-color: #f0f0f0;
}





/* 테이블 스크롤 컨테이너 */
.table-scroll-container {
    overflow-x: auto;
    position: relative;
    border: 1px solid #dee2e6;
}

/* 테이블 기본 설정 */
.table-scroll-container table {
    min-width: 800px;
    margin-bottom: 0;
}

/* 첫 번째 컬럼 고정 (체크박스) - 너비 축소 */
.table-scroll-container th:first-child,
.table-scroll-container td:first-child {
    position: sticky;
    left: 0;
    width: 40px; /* 체크박스 컬럼 너비 축소 */
    min-width: 40px;
    max-width: 40px;
    background-color: #f8f9fa;
    z-index: 10;
    border-right: 2px solid #dee2e6;
    text-align: center; /* 체크박스 중앙 정렬 */
    padding: 8px 4px !important; /* 패딩 축소 */
}

/* 두 번째 컬럼 고정 (번호) */
.table-scroll-container th:nth-child(2),
.table-scroll-container td:nth-child(2) {
    position: sticky;
    left: 40px; /* 첫 번째 컬럼 너비에 맞게 조정 */
    width: 60px; /* NO 컬럼 너비 */
    min-width: 60px;
    background-color: #f8f9fa;
    z-index: 10;
    border-right: 2px solid #dee2e6;
    text-align: center;
}

/* 헤더 고정 컬럼 배경색 */
.table-scroll-container thead th:first-child,
.table-scroll-container thead th:nth-child(2) {
    background-color: #343a40; /* thead-dark 색상 유지 */
    color: white;
}

/* 바디 고정 컬럼 배경색 */
.table-scroll-container tbody td:first-child,
.table-scroll-container tbody td:nth-child(2) {
    background-color: #ffffff;
}

/* 선택된 행의 고정 컬럼 배경색 */
.table-scroll-container tbody tr.selected td:first-child,
.table-scroll-container tbody tr.selected td:nth-child(2) {
    background-color: #fff3cd;
}

/* 체크박스 스타일 조정 */
.table-scroll-container input[type="checkbox"] {
    margin: 0;
    transform: scale(0.9); /* 체크박스 크기 약간 축소 */
}

/* 스크롤바 스타일 */
.table-scroll-container::-webkit-scrollbar {
    height: 8px;
}

.table-scroll-container::-webkit-scrollbar-track {
    background: #f1f1f1;
}

.table-scroll-container::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
}

.table-scroll-container::-webkit-scrollbar-thumb:hover {
    background: #555;
}

</style>

<script type="text/javascript">

window.selectedRowData = {};

//페이지 로드 시 그리드들 초기화
$(document).ready(function() {
 // 첫 번째 그리드 초기화
 initTreeGrid({
     gridId: 'grid1',
     searchFormId: 'searchForm1',
     templateId: 'node-row-template-1',
     addRowPosition: 'top',      // 행 추가 위치
     addChildPosition: 'bottom', // 자식행 추가 위치
     urlParamKey: 'no', // 목록의 key값설정
     urls: {
    	 mainUrl: '/sample/grid/parent2',
    	 childrenUrl: '/sample/grid/children/{no}',
    	 saveUrl: '/sample/grid/save',
    	 deleteUrl: '/sample/grid/delete'
     },
     defaultFields: { 
         name: '',
         date: '' 
     },
     pageSize: 5,
     treeLoadMode:  'full',   //'full : 트리 전체로딩' 또는 'lazy(클릭시로딩)'
     onRowClick: function(rowData, $row) {
         console.log('선택된 행:', rowData);
         // 전역 객체에 rowData 저장
         window.selectedRowData = rowData;
     },
     
     contextMenuEnabled: true,
     contextMenuItems: [
         {
             action: 'edit',
             label: '수정',
             icon: 'fas fa-edit',
             shortcut: 'F2'
         },
         {
             action: 'add-child',
             label: '자식 추가',
             icon: 'fas fa-plus',
             condition: (node) => node.childYn === 'Y' || node.level < 3,
         },
         {
             separator: true
         },
         {
             action: 'copy-json',
             label: 'JSON으로 복사',
             icon: 'fas fa-copy',
             shortcut: 'Ctrl+C'
         },
         {
             action: 'copy-tab',
             label: '탭 구분 텍스트로 복사',
             icon: 'fas fa-clipboard'
         },
         {
             action: 'copy-text',
             label: '텍스트로 복사',
             icon: 'fas fa-file-text'
         },
         {
             separator: true
         },
         {
             action: 'delete',
             label: '삭제',
             icon: 'fas fa-trash',
             shortcut: 'Del',
             disabled: (node) => node.level === 0 && node.children?.length > 0
         }
     ],
     onContextMenuClick: function(action, nodeId, node, $row, gridManager) {
         console.log('Context menu clicked:', action, nodeId);
         
         switch(action) {
             case 'edit':
                 // 수정 로직
                 break;
             case 'add-child':
                 gridManager.addChildRow(nodeId);
                 break;
             case 'copy-json':
                 if (gridManager.copyRowToClipboard(nodeId, 'json')) {
                     gridManager.showToast('JSON 형태로 클립보드에 복사되었습니다.');
                 } else {
                     gridManager.showToast('복사에 실패했습니다.', 'error');
                 }
                 break;
             case 'copy-tab':
                 if (gridManager.copyRowToClipboard(nodeId, 'tab')) {
                     gridManager.showToast('탭 구분 텍스트로 클립보드에 복사되었습니다.');
                 } else {
                     gridManager.showToast('복사에 실패했습니다.', 'error');
                 }
                 break;
             case 'copy-text':
                 if (gridManager.copyRowToClipboard(nodeId, 'text')) {
                     gridManager.showToast('텍스트 형태로 클립보드에 복사되었습니다.');
                 } else {
                     gridManager.showToast('복사에 실패했습니다.', 'error');
                 }
                 break;
             case 'delete':
                 if (confirm('정말 삭제하시겠습니까?')) {
                     gridManager.deleteNodeRecursively(nodeId);
                     gridManager.renderTable();
                 }
                 break;
         }
     }
    
 });
 

});
 
</script>

<section id="section" class="section">
    <div class="main-cont-box">
        <div class="rightcolumn">
            
            <!-- 첫 번째 그리드 -->
            <div class="grid-container">
                <h4>첫 번째 트리 그리드</h4>
                
                <!-- 검색 조건 영역 -->
                <form id="searchForm1" method="post">
                    <div class="search-box">
                        <div class="row">
                            <div class="col-md-3">
                                <label for="schName1">이름</label>
                                <input type="text" class="form-control" id="schName1" name="schName" placeholder="이름을 입력하세요">
                            </div>
                            <div class="col-md-3">
                                <label for="schDateFrom1">날짜 (시작)</label>
                                <input type="text" class="form-control date-picker" id="schDateFrom1" name="schDateFrom" placeholder="YYYY-MM-DD">
                            </div>
                            <div class="col-md-3">
                                <label for="schDateTo1">날짜 (종료)</label>
                                <input type="text" class="form-control date-picker" id="schDateTo1" name="schDateTo" placeholder="YYYY-MM-DD">
                            </div>
                            <div class="col-md-3">
                                <label for="schStatus1">상태</label>
                                <select class="form-control" id="schStatus1" name="schStatus">
                                    <option value="">전체</option>
                                    <option value="Y">활성</option>
                                    <option value="N">비활성</option>
                                </select>
                            </div>
                        </div>
                        <div class="row mt-3">
                            <div class="col-12">
                                <button type="button" class="btn btn-primary btn-search">
                                    <i class="fa fa-search"></i> 조회
                                </button>
                                <button type="button" class="btn btn-secondary ml-2 btn-reset">
                                    <i class="fa fa-refresh"></i> 초기화
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
                
                <!-- 그리드 액션 버튼들 -->
                <div id="grid1-container">
                    <button type="button" class="btn btn-sm btn-primary mb-2 btn-add-row">행 추가</button>
                    <button type="button" class="btn btn-sm btn-success mb-2 btn-add-child">자식 추가</button>
                    <button type="button" class="btn btn-sm btn-danger mb-2 btn-delete">선택 삭제</button>
                    <button type="button" class="btn btn-sm btn-info mb-2 btn-save">서버로 저장</button>
                    
                    <div class="table-scroll-container">
                    <table class="table table-bordered" id="grid1">
                        <thead class="thead-dark">
                            <tr>
                                <th><input type="checkbox" class="check-all"></th>
                                 <th>NO</th>
                                <th>이름</th>
                                <th>날짜</th>
                            </tr>
                        </thead>
                        <tbody id="grid1-body"></tbody>
                    </table>
                    </div>
                    
                    <nav>
                        <ul class="pagination" id="grid1-pagination"></ul>
                        <div id="grid1-page-info" class="page-info"></div>
                    </nav>
                </div>
                <!-- 
                <button type="button" class="btn btn-sm btn-primary mb-2" onclick="test1();">행 추가1</button>
                <button type="button" class="btn btn-sm btn-primary mb-2" onclick="test2();">자식행 추가1</button>
                 -->
            </div>
            
        </div>
    </div>
</section>
<!-- 
	라디오 추가시
			<input type="radio" name="name_{{no}}" value="Y" data-field="name" data-value="{{name}}"> 활성
            <input type="radio" name="name_{{no}}" value="N" data-field="name" data-value="{{name}}"> 비활성
 -->
<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">
    <tr class="{{displayClass}}" data-level="{{level}}" data-parent-path="{{parentPath}}">
        <td><input type="checkbox" class="row-check"></td>
 		<td>{{no}}</td>
        <td style="{{indentStyle}}">
          <span class="{{toggleClass}}" >{{toggleSymbol}}</span>
          <input type="text" name="name" class="form-control form-control-sm" data-field="name" data-value="{{name}}">
        </td>
        <td>
            <input type="text" class="form-control form-control-sm date-input" date-input" data-field="date" data-value="{{date}}">
        </td>
    </tr>
</script>



<!-- JSP 인클루드 파일들 (필요시) -->
<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
<jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>

<script>


function test1(){
	const newRowId = gridManagers['grid1'].addRow();
	console.log('새로 추가된 행 ID:', newRowId);
}

function test2(){
	const newChildId = gridManagers['grid1'].addChildToSelected();
	if (newChildId) {
	    console.log('새로 추가된 자식 행 ID:', newChildId);
	}
}


// 특정 그리드에 접근하는 방법
function getGridManager(gridId) {
    return gridManagers[gridId];
}

// 모든 그리드를 새로고침하는 함수
function refreshAllGrids() {
    Object.keys(gridManagers).forEach(gridId => {
        gridManagers[gridId].fetchData();
    });
}

// 특정 그리드만 새로고침하는 함수
function refreshGrid(gridId) {
    if (gridManagers[gridId]) {
        gridManagers[gridId].fetchData();
    }
}


</script>