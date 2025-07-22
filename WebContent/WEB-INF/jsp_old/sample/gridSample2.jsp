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
</style>

<script type="text/javascript">


//페이지 로드 시 그리드들 초기화
$(document).ready(function() {
 // 첫 번째 그리드 초기화
 initTreeGrid({
     gridId: 'grid1',
     searchFormId: 'searchForm1',
     templateId: 'node-row-template-1',
     addRowPosition: 'top',      // 행 추가 위치 top,bottom
    // addChildPosition: 'bottom', // 자식행 추가 위치
     urlParamKey: 'no', // 목록의 key값설정
     urls: {
    	 mainUrl: '/sample/grid/parent',
    	 childrenUrl: '/sample/grid/children/{no}',
    	 saveUrl: '/sample/grid/save',
    	 deleteUrl: '/sample/grid/delete'
     },
     defaultFields: { 
         name: '',
         date: '' 
     },
     pageSize: 5,
     
     onRowClick: function(rowData, $row) {
         console.log('선택된 행:', rowData);
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
                    <button type="button" class="btn btn-sm btn-danger mb-2 btn-delete">선택 삭제</button>
                    <button type="button" class="btn btn-sm btn-info mb-2 btn-save">서버로 저장</button>
                    
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
                    
                    <nav>
                        <ul class="pagination" id="grid1-pagination"></ul>
                        <div id="grid1-page-info" class="page-info"></div>
                    </nav>
                </div>
            </div>
            
          
            
        </div>
    </div>
</section>

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
// 사용 예시: 추가 그리드 초기화 방법
/*
$(document).ready(function() {
    // 세 번째 그리드 추가 예시
    initTreeGrid({
        gridId: 'grid3',
        searchFormId: 'searchForm3',
        templateId: 'node-row-template-3',
        urls: {
            parent: '/sample/grid3/parent',
            children: '/sample/grid3/children/{id}',
            save: '/sample/grid3/save'
        },
        pageSize: 15
    });
});
*/

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