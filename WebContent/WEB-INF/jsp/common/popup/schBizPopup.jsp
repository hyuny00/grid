<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<script type="text/javascript">
		
$(document).ready(function() {
	// $('#schBizNm').val(`${params.evlUserSn}`);

	var gridData ={schBizNm:$("#schBizNm").val(), crtrYr:$("#crtrYr").val()};
	console.log('gridData:::: ', gridData);
	var gridInstance = initTreeGrid({
	    gridId: 'bizPopGrid',
	    searchFormId: 'searchPopForm',
	    templateId: 'node-row-template-biz-pop',
	    urlParamKey: 'bizId', // 목록의 key값설정
	    addRowPosition: 'top',      // 행 추가 위치
	    addChildPosition: 'bottom', // 자식행 추가 위치
	    urls: {
	   	 mainUrl: '/common/schBizPopupAjax',  //초기 url
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
	    	
	    	// $("#evlPicSn").val(dbRowData.userSn);
	    	// $("#evlPicNm").val(dbRowData.userNm);
	        // $("#evlPicTelno").val(dbRowData.userTelno);

	       // var tdId = '${params.id}'
	       // var $trId = $("#"+tdId).parent();
	       // // $trId.find('td:first').text();
	       // $trId.find('td:nth-child(2)').text(dbRowData.bizId);
	       // $trId.find('td:nth-child(3)').find('[name=bizId]').val(dbRowData.bizId);
	       // $trId.find('td:nth-child(3)').find('[name=dsctnBizKornNm]').val(dbRowData.dsctnBizKornNm);
	       // $trId.find('td:nth-child(4)').text(dbRowData.crtrYr);
	       // $trId.find('td:nth-child(5)').text(dbRowData.bizPrdBgngYr);
	       // $trId.find('td:nth-child(6)').text(dbRowData.bizPrdEndYr);
	       // $trId.find('td:nth-child(7)').text(dbRowData.yyOdaSclAmt);
	       // $trId.find('td:nth-child(8)').text(dbRowData.crtrCrncNm);
	       // if(dbRowData.crtrCrncCd == 'USD'){
		   //     $trId.find('td:nth-child(9)').text(dbRowData.bizBgtSumUsdAmt);
	       // } else {
		   //     $trId.find('td:nth-child(9)').text(dbRowData.bizBgtSumKcurAmt);
	       // }
	        
	        var id = '${params.id}'
        	// const gridManager = gridManagers['grid1'];
           	// gridManager.updateRowData(id, {
           	// 	bizId: 				dbRowData.bizId,
           	// 	dsctnBizKornNm: 	dbRowData.dsctnBizKornNm,
           	// 	yyCrtrYr: 			dbRowData.crtrYr,
           	// 	bizPrdBgngYr: 		dbRowData.bizPrdBgngYr,
           	// 	bizPrdEndYr: 		dbRowData.bizPrdEndYr,
           	// 	yyOdaSclAmt: 		dbRowData.yyOdaSclAmt,
           	// 	crtrCrncNm: 		dbRowData.crtrCrncNm,
           	// 	bizBgtSumUsdAmt: 	dbRowData.bizBgtSumUsdAmt,
           	// 	bizBgtSumKcurAmt: 	dbRowData.bizBgtSumKcurAmt,
           	// });
           	
           	fn_CallBackBizPop(id, dbRowData);
	        
	    	$(".close").click();
	    }
	});
	
	$('#schBizNm').on('keyup', function(e){
		e.preventDefault();
		e.stopPropagation();
		if(e.keyCode == 13){
    		$('.btn-search').click();
    	} else {
    		return;
    	}
    });
	
	$('#btnSelected').on('click', function(e){
		const bizPopGrid = gridManagers["bizPopGrid"];
    	const chk = bizPopGrid.getCheckedCount();
    	// 체크된 행의 전체 데이터 가져오기
    	const checkedRows = bizPopGrid.getCheckedRowsData();
        console.log('checkedRows::: ', checkedRows);
        
        if(chk != 1){
        	alert("사업은 하나만 선택해 주세요.");
        	return;
        }
        
        
	     // 1. 인덱스로 행 데이터 가져오기 (0부터 시작)
	
	    //  const index = bizPopGrid.getIndexByNodeId('some-node-id'); // nodeId의 인덱스
	    //  
	    //  // 2. 인덱스로 행 데이터 업데이트 
	    //  bizPopGrid.updateRowDataByIndex(0, {
	    // 	 dsctnBizKornNm: 'data',
	    //  });
	    // 
	    //  bizPopGrid.updateRowData('row-id-123', {
    	//     name: '새로운 이름',
    	//     email: 'new@email.com',
    	//     status: 'active'
    	// });        
        
        // $("#evlPicSn").val(checkedRows[0].userSn);
        // $("#evlPicNm").val(checkedRows[0].userNm);
        // $("#evlPicTelno").val(checkedRows[0].userTelno);
        
        

        var id = '${params.id}'
       	// const gridManager = gridManagers['grid1'];
       	// gridManager.updateRowData(id, {
       	// 	bizId: checkedRows[0].bizId,
       	// 	dsctnBizKornNm: checkedRows[0].dsctnBizKornNm,
       	// 	yyCrtrYr: checkedRows[0].crtrYr,
       	// 	bizPrdBgngYr: checkedRows[0].bizPrdBgngYr,
       	// 	bizPrdEndYr: checkedRows[0].bizPrdEndYr,
       	// 	yyOdaSclAmt: checkedRows[0].yyOdaSclAmt,
       	// 	crtrCrncNm: checkedRows[0].crtrCrncNm,
       	// 	bizBgtSumUsdAmt: checkedRows[0].bizBgtSumUsdAmt,
       	// 	bizBgtSumKcurAmt: checkedRows[0].bizBgtSumKcurAmt,
       	// });
       	
       	fn_CallBackBizPop(id, checkedRows[0]);
        
    	$(".close").click();
    });

	$('.btn-search').on('click', function(e){
		var gridData ={schBizNm:$("#schBizNm").val()};
		gridInstance.setGridData(gridData);
		gridInstance.fetchData();
    });
	
	$("#schBizNm").focus();
});

</script>
<div class="popup lg" id="schBizPopup" style="display: none;">
    <div class="tit">
        <p>사업 조회</p>
        <button type="button" class="btn-close"><span class="sr-only">팝업창 닫기</span></button>
    </div>
    <form id="searchPopForm" method="post" onsubmit="return false;">
	       
	    <div class="sch">
	        <div class="flex1">
	        	<!-- 
	            <p>기준년도</p>
	        	 -->
	            <input type="hidden" class="w100" id="crtrYr" name="crtrYr" value="<c:out value="${params.crtrYr}"/>" readonly="readonly">
	            <p>사업명</p>
	            <input type="text" class="w100" id="schBizNm" name="schBizNm" value="<c:out value="${params.schBizNm}"/>" >
	            <div class="btn-wrap">
	                <button type="button" class="btn-sch btn-search">조회</button>
	            </div>
	        </div>
	    </div>
	</form>
    
    <div class="cont" id="bizPopGrid-container">
        <div class="schList">
            <div class="tblBox">
                <table class="tbl col resizable-table" id="bizPopGrid">
                    <caption></caption>
                    <colgroup>
                        <col style="width: 5%;">
                        <col style="width: 20%;">
                        <col style="width: 30%;">
                        <col style="width: 7%;">
                        <col style="width: 10%;">
                        <col style="width: 10%;">
                        <col style="width: 12%;">
                        <col style="width: 8%;">
                        <col style="width: 8%;">
                    </colgroup>
                    <thead>
                        <tr>
                            <th scope="col">
                                <div class="tblChk">
                                    <input type="checkbox" id="chk00" class="check-all"><label for="chk00"></label>
                                </div>
                                <div class="resizer"></div>
                            </th>
                            <th scope="col">사업번호<div class="resizer"></div></th>
                            <th scope="col">사업명<div class="resizer"></div></th>
                            <th scope="col">기준년도<div class="resizer"></div></th>
                            <th scope="col">사업기간<div class="resizer"></div></th>
                            <th scope="col">시행기관<div class="resizer"></div></th>
                            <th scope="col">수원국<div class="resizer"></div></th>
                            <th scope="col">사업분야<div class="resizer"></div></th>
                            <th scope="col">사업상태</th>
                        </tr>
                    </thead>
                    <tbody id="bizPopGrid-body">
                    </tbody>
                </table>
            </div>

            <!-- pagination -->
	        <div class="pagination" id="bizPopGrid-pagination">
	           
	        </div>
            <!-- //pagination -->
        </div>
    </div>
    
    <div class="btn-wrap">
        <button type="button" class="btn close">취소</button>
        <button type="button" class="btn navy" id="btnSelected">선택</button>
    </div>
</div>
<script type="text/html" id="node-row-template-biz-pop">
	<tr class="{{displayClass}}" data-level="{{level}}" data-parent-path="{{parentPath}}">
	   	<td>
	   	    <div class="tblChk">
            	<input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}"></label>
	   	    </div>
	   	</td>
	   	<td class="tC">{{bizId}}</td>
        <td class="tL">{{dsctnBizKornNm}}</td>
        <td class="tC">{{crtrYr}}</td>
        <td class="tC">{{bizPrdBgngYr}} ~ {{bizPrdEndYr}}</td>
        <td class="tC">{{enfcInstNm}}</td>
        <td class="tC">{{rcntnNtnNm}}</td>
        <td class="tC">{{bizFldNm}}</td>
        <td class="tC">{{bizStpNm}}</td>
	</tr>
</script>