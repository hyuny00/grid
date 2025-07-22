<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>


<script type="text/javascript">


//페이지 로드 시 그리드들 초기화
$(document).ready(function() {
 // 첫 번째 그리드 초기화
 initTreeGrid({
     gridId: 'grid1',
     searchFormId: 'searchForm',
     templateId: 'node-row-template-1',
     urlParamKey: 'projectId', // 목록의 key값설정
     urlParentParamKey: 'parentProjectId', // 목록의 부모key값설정,// treeLoadMode:  'full' 일경우 세팅
     addRowPosition: 'top',      // 행 추가 위치
     addChildPosition: 'bottom', // 자식행 추가 위치
     urls: {
    	 mainUrl: '/sample/newSampleList3',
    	 saveUrl: '/sample/grid/save',
    	 deleteUrl: '/sample/grid/delete'
     }, 
    // pageSize: 10,
     treeLoadMode:  'full',   //'full : 트리 전체로딩' 또는 'lazy(클릭시 로딩)'
     onRowClick: function(rowData, $row) {
         console.log('선택된 행:', rowData);
     }
 });
 

});
 
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
    <td>{{country}}</td>
 	<td><input type="text" class="date-input pickr flatpickr-input active" placeholder="날짜를 선택하세요"   data-field="regDate"   data-value="{{regDate}}" readonly="readonly"></td>
    <td>  <div class="selectBox">
<select class="form-control form-control-sm" data-field="statusCd"   data-value="{{statusCd}}">
                <option value="">선택하세요</option>
                <option value="01">카테고리A</option>
                <option value="02">카테고리B</option>
            </select>
</div>
	</td>
</tr>



</script>