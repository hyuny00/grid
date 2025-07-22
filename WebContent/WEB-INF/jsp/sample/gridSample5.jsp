<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<script type="text/javascript">
$(document).ready(function() {
    const excelValidationRules = {
        'no': {
            type: 'integer', required: true, min: 1, max: 9999, allowedValues: ['22', '33', '44', '56']
        },
        'name': {
            type: 'string', required: true, minLength: 2, maxLength: 50, allowedValues: ['홍길동1', '홍길동2']
        },
        'email': { type: 'email', required: false },
        'date': { type: 'date', required: false },
        'date2': { type: 'date', required: true }
    };

    initTreeGrid({
        gridId: 'grid1',
        searchFormId: 'searchForm1',
        urls: {
            mainUrl: '/sample/grid/parent',
            saveExlUrl: '/sample/grid/saveExl'
        },
        templateId: 'node-row-template-1',
        excelUploadEnabled: true,
        excelFileInputId: 'excel-file-input-1',
        excelUploadBtnId: 'btn-excel-upload-1',
        excelValidationRules: excelValidationRules,
        isExcelMode: true
    });
});
</script>

<div class="pgtBox">
    <div class="lt">
        <h2>엑셀 업로드 샘플</h2>
    </div>
    <ul class="breadcrumb">
        <li class="home"><a href="javascript:;">홈</a></li>
        <li><a href="javascript:;">샘플</a></li>
        <li><a href="javascript:;">엑셀 업로드</a></li>
    </ul>
    <div class="rt"></div>
</div>

<form id="searchForm1" method="post">
    <jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
    <jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>

    <div class="schBox col">
        <div class="searchFlt">
            <div class="schCol">
                <ul>
                    <li>
                        <p>이름</p>
                        <input type="text" id="schName1" name="schName" placeholder="이름을 입력하세요" class="w100">
                    </li>
                    <li>
                        <p>날짜 (시작)</p>
                        <input type="text" id="schDateFrom1" name="schDateFrom" placeholder="YYYY-MM-DD" class="w100 date-picker">
                    </li>
                    <li>
                        <p>날짜 (종료)</p>
                        <input type="text" id="schDateTo1" name="schDateTo" placeholder="YYYY-MM-DD" class="w100 date-picker">
                    </li>
                    <li>
                        <p>상태</p>
                        <select id="schStatus1" name="schStatus" class="w100">
                            <option value="">전체</option>
                            <option value="Y">활성</option>
                            <option value="N">비활성</option>
                        </select>
                    </li>
                </ul>
            </div>
        </div>
        <div class="btn-wrap">
            <button type="button" class="btn-apply btn-search">조회</button>
            <button type="reset" class="btn-reset"><span class="sr-only">초기화</span></button>
        </div>
    </div>
</form>

<div id="grid1-container">
    <div class="schList">
        <div class="titBox">
            <div class="lt"><h4>첫 번째 트리 그리드</h4></div>
            <div class="rt">
                <div class="btn-wrap">
                    <input type="file" id="excel-file-input-1" accept=".xlsx,.xls" style="display:none;">
                     <button type="button" class="btn-save txt btn-save">저장</button>
					 <button type="button" class="btn excel"  id="btn-excel-upload-1">엑셀업로드</button>
					 <button type="button" class="btn-del btn-delete"><span class="sr-only">삭제</span></button>
                    
                    
                </div>
            </div>
        </div>

        <div class="tblBox">
            <table class="tbl col" id="grid1">
                <colgroup>
                    <col style="width: 5%;">
                    <col style="width: 10%;">
                    <col style="width: 40%;">
                    <col style="width: 25%;">
                </colgroup>
                <thead>
                    <tr>
                         <th scope="col">
		                        <div class="tblChk">
		                            <input type="checkbox" id="chk01" class="check-all"><label for="chk01"></label>
		                        </div>
		                    </th>
                        <th>NO</th>
                        <th>이름</th>
                        <th>날짜</th>
                    </tr>
                </thead>
                <tbody id="grid1-body"></tbody>
            </table>

            <div class="pagination" id="grid1-pagination"></div>
            <div class="page-info" id="grid1-page-info"></div>
        </div>
    </div>
</div>

<!-- 템플릿 -->
<script type="text/html" id="node-row-template-1">
<tr>
      <td>
        <div class="tblChk">
            <input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}"></label>
        </div>
    </td>
    <td data-field="no">{{no}}</td>
    <td data-field="name">
        <input type="text" name="name" class="form-control form-control-sm" data-field="name" data-value="{{name}}">
    </td>
    <td data-field="date">
        <input type="text" class="form-control form-control-sm date-input" data-field="date" data-value="{{date}}">
    </td>
</tr>
</script>


<script>
function getGridManager(gridId) {
    return gridManagers[gridId];
}

function refreshAllGrids() {
    Object.keys(gridManagers).forEach(gridId => {
        gridManagers[gridId].fetchData();
    });
}

function refreshGrid(gridId) {
    if (gridManagers[gridId]) {
        gridManagers[gridId].fetchData();
    }
}
</script>
