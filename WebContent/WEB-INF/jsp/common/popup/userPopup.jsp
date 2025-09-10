<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<script type="text/javascript">
		
$(document).ready(function() {
	// $('#schUserNm').val(`${params.evlUserSn}`);

	var gridData ={schUserNm:$("#schUserNm").val()};
	console.log('gridData:::: ', gridData);
	var gridInstance = initTreeGrid({
	    gridId: 'userPopGrid',
	    searchFormId: 'searchPopForm',
	    templateId: 'node-row-template-userPop',
	    urlParamKey: 'bizId', // 목록의 key값설정
	    addRowPosition: 'top',      // 행 추가 위치
	    addChildPosition: 'bottom', // 자식행 추가 위치
	    urls: {
	   	 mainUrl: '/common/userPopupAjax',  //초기 url
		 childrenUrl: '', // 트리구조사용시. 부모클릭시 자식 불러올때, lazy(클릭시 로딩)(option)
		 saveUrl: '', //(option) 저장url
		 deleteUrl: '' //(option)삭제url 사용시 deleteUrl로 삭제. 사용하지않으면 저장시 삭제정보 서버로 전송
	    }, 
	    pageSize: 10,
	 	gridData: gridData, //(option) 초기로딩시 별도조건 필요할경우
	    onRowClick: function(rowData, $row) {
	        console.log('선택된 행:', rowData);
	        
	        // $("#evlUserSn").val(rowData.userNm);
	        // $("#evlUserTelno").val(rowData.deptTelno);
	    },
	    onRowDoubleClick: function(dbRowData, $row) {
	    	console.log('dbRowData  행:', dbRowData);
	    	
	    	$("#evlPicSn").val(dbRowData.userSn);
	    	$("#evlPicNm").val(dbRowData.userNm);
	        $("#evlPicTelno").val(dbRowData.userTelno);
	        $("#evlPicNm").focus();
	    	$(".close").click();
	    }
	});
	
	$('#schUserNm').on('keyup', function(e){
		e.preventDefault();
		e.stopPropagation();
		if(e.keyCode == 13){
    		$('.btn-search').click();
    	} else {
    		return;
    	}
    });
	
	$('#btnSelected').on('click', function(e){
		const gridManager=gridManagers["userPopGrid"];
    	const chk = gridManager.getCheckedCount();
    	// 체크된 행의 전체 데이터 가져오기
    	const checkedRows = gridManager.getCheckedRowsData();
        console.log('checkedRows::: ', checkedRows);
        
        if(chk != 1){
        	alert("담당자는 한 명만 선택해 주세요.");
        	return;
        }
        
        $("#evlPicSn").val(checkedRows[0].userSn);
        $("#evlPicNm").val(checkedRows[0].userNm);
        $("#evlPicTelno").val(checkedRows[0].userTelno);
        $("#evlPicNm").focus();
    	$(".close").click();
    });
	
	$('.btn-search').on('click', function(e){
		var gridData ={schUserNm:$("#schUserNm").val()};
		gridInstance.setGridData(gridData);
		gridInstance.fetchData();
    });
	
	$("#schUserNm").focus();
});

</script>
<div class="popup" id="userPopup" style="display: none;">
    <div class="tit">
        <p>담당자 조회</p>
        <button type="button" class="btn-close"><span class="sr-only">팝업창 닫기</span></button>
    </div>
    <form id="searchPopForm" method="post" onsubmit="return false;">
	       
	    <div class="sch">
	        <div class="flex1">
	            <p>성명</p>
	            <input type="text" class="w100" id="schUserNm" name="schUserNm" value="<c:out value="${params.schUserNm}"/>" >
	            <div class="btn-wrap">
	                <button type="button" class="btn-sch btn-search">조회</button>
	            </div>
	        </div>
	    </div>
	</form>
    
    <div class="cont" id="userPopGrid-container">
        <div class="schList">
            <div class="tblBox">
                <table class="tbl col" id="userPopGrid">
                    <caption></caption>
                    <colgroup>
                        <col style="width: 5%;">
                        <col style="width: 20%;">
                        <col style="width: 35%;">
                        <col style="width: 20%;">
                        <col style="width: 20%;">
                    </colgroup>
                    <thead>
                        <tr>
                            <th scope="col">
                                <div class="tblChk">
                                    <input type="checkbox" id="chk00" class="check-all"><label for="chk00"></label>
                                </div>
                            </th>
                            <th scope="col">성명</th>
                            <th scope="col">시행기관</th>
                            <th scope="col">부서</th>
                            <th scope="col">전화번호</th>
                        </tr>
                    </thead>
                    <tbody id="userPopGrid-body">
                    </tbody>
                </table>
            </div>

            <!-- pagination -->
	        <div class="pagination" id="userPopGrid-pagination">
	           
	        </div>
            <!-- //pagination -->
        </div>
    </div>
    
    <div class="btn-wrap">
        <button type="button" class="btn close">취소</button>
        <button type="button" class="btn navy" id="btnSelected">선택</button>
    </div>
</div>
<script type="text/html" id="node-row-template-userPop">
	<tr class="{{displayClass}}" data-level="{{level}}" data-parent-path="{{parentPath}}">
	   	<td>
	   	    <div class="tblChk">
            	<input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}"></label>
	   	    </div>
	   	</td>
	   	<td class="tC">{{userNm}}</td>
        <td class="tL">{{ogdpInstNm}}</td>
        <td class="tL">{{ogdpDeptNm}}</td>
        <td class="tC">{{userTelno}}</td>
	</tr>
</script>