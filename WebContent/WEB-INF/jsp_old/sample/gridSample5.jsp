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


/* 오류 셀 스타일 */
.excel-error-cell {
   //background-color: #ffebee !important;
    //border: 2px solid #f44336 !important;
    color: #d32f2f !important;
}

.excel-error-cell:focus {
    background-color: #ffcdd2 !important;
    border-color: #d32f2f !important;
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


//페이지 로드 시 그리드들 초기화
$(document).ready(function() {
	
	
	 // 엑셀 유효성 검사 규칙
   const excelValidationRules = {
        'no': {
            type: 'integer',
            required: true,
            min: 1,
            max: 9999,
            allowedValues: ['22', '33', '44', '56']
        },
        'name': {
            type: 'string',
            required: true,
            minLength: 2,
            maxLength: 50,
            allowedValues: ['홍길동1', '홍길동2']
        },
        'email': {
            type: 'email',
            required: false
        },
        'date': {
            type: 'date',
            required: false
        },
        'date2': {
            type: 'date',
            required: true
        },
        /*,
        'phone': {
            type: 'phone',
            required: false,
            pattern: '^[\d-]+$',
            patternMessage: '숫자와 하이픈만 입력 가능합니다.'
        },
        'status': {
            type: 'string',
            required: true,
            allowedValues: ['Y', 'N', '대기', '완료', '취소']
        },
        'regDate': {
            type: 'date',
            required: false
        },
        'score': {
            type: 'number',
            required: false,
            min: 0,
            max: 100
        }
        */
    }
	
	
	const gridManager1 = initTreeGrid({
	    gridId: 'grid1',
	    searchFormId: 'searchForm1',
	    urls: {
	    	 mainUrl: '/sample/grid/parent',
	    	 saveExlUrl: '/sample/grid/saveExl',
	    },
	    templateId: 'node-row-template-1',
	    // 엑셀 업로드 설정 추가
	    excelUploadEnabled: true,
	    excelFileInputId: 'excel-file-input-1',
	    excelUploadBtnId: 'btn-excel-upload-1',
	    excelValidationRules : excelValidationRules,
	    isExcelMode : true
	    
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
                   
                    
                     <div class="grid-toolbar">
					        <button type="button" class="btn btn-sm btn-danger mb-2 btn-delete">선택 삭제</button>
					          <button type="button" class="btn btn-sm btn-info mb-2 btn-save">서버로 저장</button>
					        <button type="button" class="btn btn-primary" id="btn-excel-upload-1">엑셀 업로드</button>
					        <input type="file" id="excel-file-input-1" accept=".xlsx,.xls" style="display: none;">
					 </div>
    
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
            </div>
            
		
		<!-- JSP 인클루드 파일들 (필요시) -->
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>
            
            
            
        </div>
    </div>
</section>

<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">
    <tr >
        <td><input type="checkbox" class="row-check"></td>
 		<td data-field="no">{{no}}</td>
        <td data-field="name">
           <input type="text" name="name" class="form-control form-control-sm" data-field="name" data-value="{{name}}"> 
        </td>
        <td data-field="date">
        	<input type="text" class="form-control form-control-sm date-input" date-input" data-field="date" data-value="{{date}}">
        </td>

    </tr>
</script>


<script>


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