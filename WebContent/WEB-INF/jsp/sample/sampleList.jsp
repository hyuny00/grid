<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<style>
    .popup-modal {
      position: fixed;
      top: 0; left: 0;
      width: 100%; height: 100%;
      background: rgba(0,0,0,0.5);
    }
    .popup-content {
      position: absolute;
      top: 50%; left: 50%;
      transform: translate(-50%, -50%);
      background: white;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.3);
      overflow: hidden;
    }
    .popup-close {
      position: absolute;
      top: 8px; right: 12px;
      font-size: 20px;
      cursor: pointer;
      z-index: 10000;
    }
  </style>
<script type="text/javascript">
$(document).ready(function() {

    // 트리 그리드 초기화
    initTreeGrid({
        gridId: 'grid1',
        searchFormId: 'searchForm',
        templateId: 'node-row-template-1',
        urls: {
            mainUrl: '/sample/selectSampleList',
        },
        pageSize: 3,
        onRowClick: function(rowData, $row) {
            console.log('선택된 행:', rowData);
            sampleDetail(rowData.id);
        },
        
        onRowDoubleClick: function(rowData, $row) {
            console.log('더블클릭된 행:', rowData);
            // 여기에 더블클릭 시 실행할 로직 추가
            // 예: 상세 페이지 이동, 수정 모달 열기 등
        }
    });

    // select2 초기화
    $('#test').select2({ width: '20%' });
    $('#test2').select2({ maximumSelectionLength: 2 });
    $('#test3').select2();

    // 엑셀 다운로드
    $("#btn-excel").on("click", function () {
        var $preparingFileModal = $("#preparing-file-modal");
        $preparingFileModal.dialog({ modal: true });
        $("#progressbar").progressbar({ value: false });

        $.fileDownload("/file/largeExcelDown", {
            httpMethod: "post",
            data: $("#searchForm").serialize(),
            successCallback: function () {
                $preparingFileModal.dialog('close');
            },
            failCallback: function () {
                $preparingFileModal.dialog('close');
                $("#error-modal").dialog({ modal: true });
            }
        });

        return false;
    });
});

function sampleDetail(id) {
    document.searchForm.id.value = id;
    document.searchForm.action = "${basePath}/sample/selectSample";
    document.searchForm.submit();
}

function sampleForm() {
    document.searchForm.action = "${basePath}/sample/sampleForm";
    document.searchForm.submit();
}

function selectCode() {
    var code = $("#test option:selected").data("code");
    var cdGroupSn = $("#test option:selected").data('code-group');

    $.ajax({
        url: '/common/selectCode',
        type: 'get',
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        data: { code: code, cdGroupSn: cdGroupSn },
        success: function(data) {
            $('#test3').empty();
            if (data) {
                data.unshift({ id: "", text: "선택" });
                $('#test3').select2({ placeholder: '선택', data: data });
            }
        }
    });
}




</script>

<div class="pgtBox">
    <div class="lt">
        <h2>샘플 목록</h2>
    </div>
    <ul class="breadcrumb">
        <li class="home"><a href="javascript:;">홈</a></li>
        <li><a href="javascript:;">샘플</a></li>
        <li><a href="javascript:;">목록</a></li>
    </ul>
    <div class="rt"></div>
</div>

<form id="searchForm" name="searchForm" method="post" action="${basePath}/sample/selectSampleList">
    <jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
    <input type="hidden" name="id" value="">

    <div class="schBox col">
        <div class="searchFlt">
            <div class="schCol">
                <ul>
                    <li>
                        <p>검색조건</p>
                        <select name="schCondition" id="schCondition">
                            <option value="1" <c:if test="${param.schCondition eq '1'}">selected</c:if>>Name</option>
                            <option value="0" <c:if test="${param.schCondition eq '0'}">selected</c:if>>ID</option>
                        </select>
                    </li>
                    <li>
                        <p>키워드</p>
                        <input type="text" name="schKeyword" id="schKeyword" value="${param.schKeyword}" />
                    </li>
                    <li>
                        <p>분류</p>
                        <select name="test" id="test" onchange="selectCode();">
                            <option value="">전체</option>
                            <c:forEach var="code" items="${sampleCodeList}">
                                <option value="${code.code}" data-code="${code.code}" data-code-group="${code.cdGroupSn}">${code.value}</option>
                            </c:forEach>
                        </select>

                        <select name="test3" id="test3" style="width:200px;">
                            <option value="">전체</option>
                        </select>

                        <select name="test2" id="test2" multiple="multiple" style="width:200px;">
                            <option value="01">중소기업</option>
                            <option value="02">소상공인</option>
                            <option value="03">중앙부처</option>
                            <option value="04">지자체</option>
                            <option value="05">관행</option>
                            <option value="06">현장대기</option>
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
            <div class="lt"></div>
            <div class="rt">
                <div class="btn-wrap">
                    <button type="button" class="btn excel" id="btn-excel">엑셀다운로드</button>
                    <button type="button" class="btn biz" onclick="sampleForm();">등록</button>
				    <button type="button" class="btn" onclick="openCustomPopup('pop01', '/sample/popup1')">팝업1</button>
				    <button type="button" class="btn" onclick="openCustomPopup('pop02', '/sample/popup2',{id:'2', name:'aaa'})">팝업2</button>
         
                    
                </div>
            </div>
        </div> 

        <div class="tblBox">
            <table class="tbl col" id="grid1">
                <thead>
                    <tr>
                     <th>NO</th>
                        
                         <th scope="col" class="sortable" data-sort="id">
            					ID <span class="sort-icon"></span>
        				</th> 
        				
        				 <th scope="col" class="sortable" data-sort="name">
            					이름 <span class="sort-icon"></span>
        				</th> 
                        
                        <th>useYn</th> 
                        <th>description</th> 
                        <th>regUser</th>
                    </tr>
                </thead>
                <tbody id="grid1-body"></tbody>
            </table>

            <div class="pagination" id="grid1-pagination"></div>
        </div>
    </div>
</div>

<!-- 템플릿 -->
<script type="text/html" id="node-row-template-1">
<tr>
     <td class="tC">{{reverseIndex}}</td>
<td>{{id}}</td>
    <td><span>{{name}}</span></td>
    <td>{{useUn}}</td>
    <td>{{description}}</td>
    <td>{{regUser}}</td>
</tr>
</script>

<pre><code>

1. 날짜 포맷팅
{{format regDate "date"}}

<!-- 커스텀 날짜 포맷 -->
{{format regDate "date" pattern="YYYY/MM/DD"}}
{{format regDate "date" pattern="MM-DD-YYYY"}}
{{format regDate "date" pattern="YYYY년 MM월 DD일"}}
{{format regDate "date" pattern="YYYY-MM-DD HH:mm:ss"}}

2. 금액 포맷팅
{{format amount "currency"}}

<!-- 옵션 포함 원화 포맷 -->
{{format amount "currency" locale="ko-KR" currency="KRW"}}

<!-- 달러 포맷 -->
{{format amount "currency" locale="en-US" currency="USD"}}

<!-- 소수점 포함 -->
{{format amount "currency" minimumFractionDigits="2" maximumFractionDigits="2"}}

3. 숫자 포맷팅 (천단위 콤마)
{{format count "number"}}

<!-- 소수점 2자리까지 -->
{{format price "number" decimals="2"}}

4. 퍼센트 포맷팅
{{format rate "percent"}}

<!-- 소수점 2자리 퍼센트 -->
{{format rate "percent" decimals="2"}}

5. 문자열 포맷팅
html<!-- 대문자로 변환 -->
{{format name "string" case="upper"}}

<!-- 소문자로 변환 -->
{{format name "string" case="lower"}}

<!-- 제목 형태로 변환 -->
{{format name "string" case="title"}}






var gridManager=gridManagers["gridId"];

//그리드 체크갯수 구하기
const a=gridManager.getCheckedCount();

/ 체크된 행의 전체 데이터 가져오기
const checkedRows = gridManager.getCheckedRowsData();
console.log('체크된 행 데이터:', checkedRows);

// 체크된 행의 특정 필드만 가져오기
const checkedProjectIds = gridManager.getCheckedRowsField('projectId');
console.log('체크된 프로젝트 ID들:', checkedProjectIds);

const checkedTitles = gridManager.getCheckedRowsField('projectTitle');
console.log('체크된 프로젝트 제목들:', checkedTitles);

// ID만 가져오기
const checkedIds = gridManager.getCheckedRowIds();
console.log('체크된 행 ID들:', checkedIds);





// 그리드 매니저 인스턴스에서 사용
const gridManager = gridManagers['your-grid-id'];

// 1. 특정 행의 데이터 가져오기
const rowData = gridManager.getRowData('row-id-123');

// 2. 특정 행의 여러 필드 업데이트
gridManager.updateRowData('row-id-123', {
    name: '새로운 이름',
    email: 'new@email.com',
    status: 'active'
});

// 3. 특정 행의 단일 필드 업데이트
gridManager.updateRowField('row-id-123', 'name', '수정된 이름');




const gridManager = gridManagers['your-grid-id'];

// 1. 인덱스로 행 데이터 가져오기 (0부터 시작)
const firstRowData = gridManager.getRowDataByIndex(0);
const thirdRowData = gridManager.getRowDataByIndex(2);

// 2. 인덱스로 행 데이터 업데이트
gridManager.updateRowDataByIndex(0, {
    name: '첫 번째 행 수정',
    status: 'updated'
});

// 3. 인덱스로 특정 필드만 업데이트
gridManager.updateRowFieldByIndex(1, 'name', '두 번째 행 이름 수정');

//템플릿수정
 <span data-field="name">{{name}}</span>

// 4. 상호 변환
const nodeId = gridManager.getNodeIdByIndex(2); // 3번째 행의 nodeId
const index = gridManager.getIndexByNodeId('some-node-id'); // nodeId의 인덱스

// 5. 전역 함수로 사용
const rowData = getGridRowDataByIndex('your-grid-id', 0);
updateGridRowFieldByIndex('your-grid-id', 1, 'status', 'active');


//체크박스 행선택용 checkedAttr : "checked"
 gridManager.updateRowDataByIndex(3, {
		   projectTitle: '2222',
		   regDate:'20000101',
		   statusCd:'01',
		   checkedAttr:""
		});



</code></pre>
