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
     urls: {
    	 mainUrl: '/sample/newSampleList2',
     },
     pageSize: 10,

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
	        <button type="button" class="btn-apply">조건검색</button>
	        <button type="reset" class="btn-reset"><span class="sr-only">초기화</span></button>
	    </div>
	</div>

	<div class="schList">
	    <div class="titBox">
	        <div class="lt"></div>
	        <div class="rt">
	            <div class="btn-wrap">
	                <button type="button" class="btn excel">엑셀다운로드  </button>
	                <button type="button" class="btn hwp">한글다운로드</button>
	                <button type="button" class="btn pdf">PDF다운로드</button>
	                <button type="button" class="btn biz">사업등록</button>
	            </div>
	        </div>
	    </div>

	    <div class="tblBox">
	     <div id="grid1-container">
	        <table class="tbl col" id="grid1">
	            <caption></caption>
	            <colgroup>
	                <col style="width: 5%;">
	                <col style="width: 8%;">
	                <col style="width: 48%;">
	                <col style="width: 6%;">
	                <col style="width: 9%;">
	                <col style="width: 9%;">
	                <col style="width: 13%;">
	                <col style="width: 5%;">
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
	                    <th scope="col">사업기간</th>
	                    <th scope="col">시행기관</th>
	                    <th scope="col">수원국</th>
	                    <th scope="col">사업분야</th>
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

</form>
<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">

<tr>
    <td>
        <div class="tblChk">
            <input type="checkbox" class="row-check" data-project-id="{{projectId}}"><label for="chk01"></label>
        </div>
    </td>
    <td class="tC">{{projectId}}</td>
    <td>
        <a href="#"><span class="span orange">{{projectType}}</span>{{projectTitle}}</a>
    </td>
    <td class="tC">{{period}}</td>
    <td>{{department}}</td>
    <td>{{country}}</td>
    <td>{{sector}}</td>
    <td><span class="badge pt">{{status}}</span></td>
</tr>

</script>