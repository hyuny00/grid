<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>


<script type="text/javascript">




//페이지 로드 시 그리드들 초기화
$(document).ready(function() {
	
	


	//템플릿 생성 시 사용
/*
	const selectOption2 = {

		   statusOptions : [
		      { value: '01', text: '카테고리A' },
		      { value: '02', text: '카테고리B' }
		  ]
		  ,
		  statusOptions2 : [
		      { value: '01', text: '카테고리C' },
		      { value: '02', text: '카테고리D' }
		  ]

	}
	
	*/
	
    var requests = [
	    {codeDiv:'statusOptions', code: '', cdGroupSn: '1' },
	    {codeDiv:'statusOptions2', code: '', cdGroupSn: '1' }
	];
	
	var selectOption;

	$.ajax({
	    url: '/common/selectCodeListMultiple',
	    type: 'post',
	    contentType: "application/json; charset=UTF-8",
	    data: JSON.stringify({ requests: requests }),
	    success: function(data) {
	        console.log(JSON.stringify(data));
	        selectOption=data;
	        initializeGrid();
	    }
	});

	
	
	function initializeGrid() {
	
		 // 첫 번째 그리드 초기화
		 initTreeGrid({
		     gridId: 'grid1',
		     searchFormId: 'searchForm',
		     templateId: 'node-row-template-1',
		     urlParamKey: 'projectId', // 목록의 key값설정
		     addRowPosition: 'top',      // 행 추가 위치
		     addChildPosition: 'bottom', // 자식행 추가 위치
		     urls: {
		    	 mainUrl: '/sample/newSampleList2',
		    	 childrenUrl: '/sample/newSampleList2/children/{projectId}',
		    	 saveUrl: '/sample/grid/save',
		     }, 
		     pageSize: 10,
		     checkCount: 3, 
		     selectOption :selectOption,
		     onRowClick: function(rowData, $row) {
		         console.log('선택된 행:', rowData);
		     },
		     
		     onRowDoubleClick: function(rowData, $row) {
		         console.log('더블클릭된 행:', rowData);
		         // 여기에 더블클릭 시 실행할 로직 추가
		         // 예: 상세 페이지 이동, 수정 모달 열기 등
		     }
		 });
	}
		 

});


function test2(){
	var a= gridManagers['grid1'].getDeletedRows();
	
	console.log("...."+JSON.stringify(a));
	
	
	var gridManager = gridManagers['grid1'];
	
	
	// 체크된 행의 전체 데이터 가져오기
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
	
}
 
</script>

<div class="pgtBox">
    <div class="lt">
        <h2>요청 중인 연계</h2>
    </div>
    
    <ul class="breadcrumb">
        <li class="home"><a href="javascript:;">홈</a></li>
        <li><a href="javascript:;">평가</a></li>
        <li><a href="javascript:;">자체평가</a></li>
    </ul>
    
    <div class="rt"></div>
</div>
<form id="searchForm" method="post">
	<!-- JSP 인클루드 파일들 (필요시) -->
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>
	<div class="schBox col">
	    <div class="searchFlt">
	        <div class="schCol">
	            <ul>
	                <li>
	                    <p>프로그래명</p>
	                    <input type="text" placeholder="프로그래명을 입력하세요." class="w100">
	                </li>
	                <li>
	                    <p>장소</p>
	                    <input type="text" placeholder="장소를 입력하세요." class="w100">
	                </li>
	                <li>
	                    <p>강사명</p>
	                    <input type="text" placeholder="강사명을 입력하세요.">
	                </li>
	                <li>
	                    <p>연수기간</p>
	                    <div class="pickrBox">
	                        <input type="text" class="pickr" placeholder="날짜를 선택하세요">
	                        <p>~</p>
	                        <input type="text" class="pickr" placeholder="날짜를 선택하세요">
	                    </div>
	                </li>
	            </ul>
	        </div>
	    </div>
	    
	    <div class="btn-wrap">
	        <button type="button" class="btn-apply btn-search">조건검색</button>
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
		                <button type="button" class="btn-save txt btn-save">저장</button>
					    <button type="button" class="btn-plus txt btn-add-row">추가</button>
					    <button type="button" class="btn-plus txt btn-add-child">자식추가</button>
					    <button type="button" class="btn-del btn-delete"><span class="sr-only">삭제</span></button>
		            </div>
		        </div>
		    </div>
		    
					        
					        <a href="javascript:test2()">aaaaa</a>
		    
		    <div class="tblBox">
		 
		        <table class="tbl col" id="grid1">
		            <caption></caption>
		            <colgroup>
		                <col style="width: 5%;">
		                <col style="width: 8%;">
		                <col style="width: 28%;">
		                <col style="width: 9%;">
		                <col style="width: 10%;">
		                <col style="width: 13%;">
		            </colgroup>
		            <thead>
		                <tr>
		                    <th scope="col">
		                        <div class="tblChk">
		                            <input type="checkbox" id="chk01" class="check-all"><label for="chk01"></label>
		                        </div>
		                    </th>
		                    <th scope="col">사업번호</th>
		                    <th scope="col">사업명</th>
		                    <th scope="col">수원국</th>
		                    <th scope="col">등록일</th>
		                    <th scope="col">사업상태</th>
		                </tr>
		            </thead>
		            <tbody id="grid1-body">
		            </tbody>
		        </table>
		        
		        
			        <!-- pagination -->
			        <div class="pagination" id="grid1-pagination">
			           
			        </div>
			        <!-- //pagination -->
		     
		    </div>
		</div>
	</div>


<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">
   
<tr class="{{displayClass}}" data-level="{{level}}" data-parent-path="{{parentPath}}">
    <td>
        <div class="tblChk">
            <input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}"></label>
        </div>
    </td>
    <td class="tC">{{projectId}}</td>
     <td style="{{indentStyle}}">

      <span class="{{toggleClass}}" >{{toggleSymbol}}</span><input type="text" name="projectTitle" style="width:95%" data-field="projectTitle" data-value="{{projectTitle}}">

    </td>
    <td>{{country}}{{format regDate "date" pattern="YYYY/MM/DD"}}</td>
 	<td><input type="text" class="date-input pickr flatpickr-input active" placeholder="날짜를 선택하세요"   data-field="regDate"   data-value="{{regDate}}" readonly="readonly"></td>
    <td>  
<div class="selectBox">
    <select class="form-control form-control-sm" data-field="statusCd" data-value="{{statusCd}}">
        <option value="">선택하세요</option>
        {{#each statusOptions}}
        <option value="{{this.value}}" {{#if this.value equals statusCd}}selected{{/if}}>{{this.text}}</option>
        {{/each}}
    </select>
</div>
	</td>
</tr>

</script>