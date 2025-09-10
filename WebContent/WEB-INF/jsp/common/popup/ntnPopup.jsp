<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<script type="text/javascript">
		
$(document).ready(function() {
	// $('#schNtnNm').val(`${params.evlUserSn}`);
	
	var requests = [
	    {schCodeDiv:'cntntCd', code: '', cdGroupSn: '16' },
	];
	fn_selectCodeList(requests, fn_callBackCodeCntntList);
	
	// 공통 그리드 매니저 인스턴스 생성
    const gridManager = new CommonGridManager();
	
    var gridData ={schNtnNm:$("#schNtnNm").val()};
 	// 개별 페이지별 설정
    const pageConfig = {
        // 동적 카테고리 설정
        dynamicCategories: [],

        // 각 카테고리별 코드 그룹 매핑
        categoryCodeMapping: {
        },
     	// 2단계이상코드의 첫단계 타이틀
        categoryTitles: {
        },
     // 2단계이상 필터가 필요한 카테고리들을 정의
        multiStepCategories: [],
        // 그리드 설정들
        gridConfigs: [
            {
                gridId: 'ntnPopGrid',
                searchFormId: 'searchPopForm',
                templateId: 'node-row-template-ntnPop',
                urls: {
                	mainUrl: '/common/ntnPopupAjax', //초기 url
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

        	        const id= '${param.id}';
        	    	fn_ntnCallback(id, dbRowData);

        	    	// $("#evlPicSn").val(dbRowData.userSn);
        	    	// $("#evlPicNm").val(dbRowData.userNm);
        	        // $("#evlPicTelno").val(dbRowData.userTelno);
        	        // $("#evlPicNm").focus();
        	    	$(".close").click();
        	    },
	   		     // ★ 추가: 초기화 완료 후 실행될 콜백
	   		     onInitComplete: function() {
	   		         console.log('그리드 초기화 완료!');
	
	   		         // doSomethingAfterInit();
	   		     }
            },
        ],
    };

    // 모든 초기화 작업 시작
    const result = gridManager.initializeAllData(pageConfig)
     .then(result => {
         console.log('초기화 완료:', result);

         // 전역 변수에 저장
         filterSystem = result.filterSystem;
         gridInstances = result.gridInstances;

         // 초기화 완료 후 초기 필터 설정필요시
         setupInitialFilters();

     })
     .catch(error => {
         console.error('초기화 실패:', error);
     });
	
	$('#schCntntCd').on('change', function(e){
		
    });
	
	$('.btn-reset').on('click', function(e){
		$('#schCntntCd').val('');
		$('#schNtnNm').val('');
		$('.btn-search').click();
    });
	
	$('#schNtnNm').on('keyup', function(e){
		e.preventDefault();
		e.stopPropagation();
		if(e.keyCode == 13){
    		$('.btn-search').click();
    	} else {
    		return;
    	}
    });
	
	$('#btnSelected').on('click', function(e){
		const gridManager=gridManagers["ntnPopGrid"];
    	const chk = gridManager.getCheckedCount();
    	// 체크된 행의 전체 데이터 가져오기
    	const checkedRows = gridManager.getCheckedRowsData();
        console.log('checkedRows::: ', checkedRows);
        
        // if(chk != 1){
        // 	alert("국가는 하나만 선택해 주세요.");
        // 	return;
        // }
        const id= '${param.id}';
        // $("#evlPicSn").val(checkedRows[0].userSn);
        // $("#evlPicNm").val(checkedRows[0].userNm);
        // $("#evlPicTelno").val(checkedRows[0].userTelno);
        $("#"+id).val(dbRowData.ntnNm);
        $("#"+id).focus();
    	$(".close").click();
    });
	
	$('.btn-search').on('click', function(e){
		const gridInstance = getGridById('ntnPopGrid');
		var gridData ={schNtnNm:$("#schNtnNm").val()};
		gridInstance.setGridData(gridData);
		gridInstance.fetchData();
    });
	
	$("#schNtnNm").focus();
});

/**
 * 초기 필터 설정 함수
 */
function setupInitialFilters() {
    if (!filterSystem) {
        console.error('필터 시스템이 초기화되지 않았습니다.');
        return;
    }

    // 여러 필터 동시 설정
    /*
    filterSystem.setSimpleInitialFilters({
        "schNtnCd": { value: "229", text: "시행기관 229" },
        "period": { value: "2024-01-01,2024-01-03", text: "시작일: 2024-01-01 - 2024-01-03" }
    });
    */
    console.log('초기 필터 설정 완료');
}

function fn_selectCodeList(requests, fn) {
	$.ajax({
	    url: '/common/selectCodeListMultiple',
	    type: 'post',
	    contentType: "application/json; charset=UTF-8",
	    data: JSON.stringify({ requests: requests }),
	    success: function(data) {
	        console.log(JSON.stringify(data));
	        fn(data);
	    }
	});
}

function fn_callBackCodeCntntList(data) {
	console.log('data====>', data);
	console.log('data====>', data.cntntCd);
	
	let optTxt = "";
	optTxt += "<option value=\"\">- 선택 -</option>";
	
	if(null != data && null != data.cntntCd){
		console.log('data.cntntCd.length====>', data.cntntCd.length);
		for(let i= 0 ; i < data.cntntCd.length ; i++){
			cntnt = data.cntntCd[i];
			console.log('cntnt====>', 11);
			console.log('cntnt====>', cntnt.value);
			console.log('cntnt====>', cntnt.text);
			optTxt += "<option value=\""+cntnt.value+"\" >"+cntnt.text+"</option>";
		}
		console.log('optTxt====>', optTxt);
	}
	$("#schCntntCd").empty().append(optTxt);
}

</script>
<div class="popup lg" id="ntnPopup" style="display: none;">
    <div class="tit">
        <p>국가 조회</p>
        <button type="button" class="btn-close"><span class="sr-only">팝업창 닫기</span></button>
    </div>
    <form id="searchPopForm" method="post" onsubmit="return false;">
	    <div class="sch">
			<div>
				<p>대륙</p>
				<div class="selectBox">
					<select name="schCntntCd" id="schCntntCd">
						<option value="" hidden="">- 선택 -</option>
						<c:forEach items="${cntntCdList}" var="cntntCd" varStatus="status">
			        		<c:set var="selChk" value="" />
							<c:if test="${param.cntntCd eq  cntntCd.code}">
								<c:set var="selChk" value="selected" />
							</c:if>
				        	<option value="${cntntCd.value}" ${selChk}><c:out value="${cntntCd.value}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>

			<div class="flex1">
				<p>국가</p>
				<input type="text" class="w100" id="schNtnNm" name="schNtnNm" value="<c:out value="${params.schNtnNm}"/>" >
				<div class="btn-wrap">
					<button class="btn-reset"><span class="sr-only">초기화버튼</span></button>
					<button type="button" class="btn-sch btn-search">조회</button>
				</div>
			</div>
		</div>
	</form>
    
    <div class="cont" id="ntnPopGrid-container">
        <div class="schList">
            <div class="tblBox">
                <table class="tbl col tC resizable-table" id="ntnPopGrid">
                    <caption></caption>
                    <colgroup>
                        <col style="width: 5%;">
                        <col style="width: 10%;">
                        <col style="width: 20%;">
                        <col style="width: auto;">
                        <col style="width: 10%;">
                        <!-- 
                        <col style="width: 20%;">
                         -->
                        <col style="width: 10%;">
                        <col style="width: 10%;">
                        <col style="width: 10%;">
                        <!-- 
                        <col style="width: 20%;">
                        <col style="width: 20%;">
                        -->
                    </colgroup>
                    <thead>
                        <tr>
                            <th scope="col">
                                <div class="tblChk">
                                    <input type="checkbox" id="chk00" class="check-all"><label for="chk00"></label>
                                </div>
                                <div class="resizer"></div>
                            </th>
                            <th scope="col">대륙<div class="resizer"></div></th>
                            <th scope="col">국가<div class="resizer"></div></th>
                            <th scope="col">국가영문명<div class="resizer"></div></th>
                            <th scope="col">개별국가여부<div class="resizer"></div></th>
                            <!--  
                            <th scope="col">대외경제협력기금국가코드</th>
                            -->
                            <th scope="col">중점협력국가여부<div class="resizer"></div></th>
                            <th scope="col">고채무빈국여부<div class="resizer"></div></th>
                            <th scope="col">적격수원국여부</th>
                            <!--  
                            <th scope="col">공적개발원조국가코드</th>
                            <th scope="col">소득구분코드</th>
                            -->
                        </tr>
                    </thead>
                    <tbody id="ntnPopGrid-body">
                    </tbody>
                </table>
            </div>

            <!-- pagination -->
	        <div class="pagination" id="ntnPopGrid-pagination">
	           
	        </div>
            <!-- //pagination -->
        </div>
    </div>
    
    <div class="btn-wrap">
        <button type="button" class="btn close">취소</button>
        <button type="button" class="btn navy" id="btnSelected">선택</button>
    </div>
</div>
<script type="text/html" id="node-row-template-ntnPop">
	<tr class="{{displayClass}}" data-level="{{level}}" data-parent-path="{{parentPath}}">
	   	<td>
	   	    <div class="tblChk">
            	<input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}"></label>
	   	    </div>
	   	</td>
        <td class="tC">{{cntntNm}}</td>
	   	<td class="tC">{{ntnNm}}</td>
	   	<td class="tC">{{ntnEngNm}}</td>
        <td class="tC">{{indivNtnYn}}</td>
		 <!--  
        <td class="tL">{{edcfNtnCd}}</td>
         -->
        <td class="tC">{{emphsClbrNtnYn}}</td>
        <td class="tC">{{hipcYn}}</td>
        <td class="tC">{{elgbRcntnYn}}</td>
		<!--  
        <td class="tC">{{odaNtnCd}}</td>
        <td class="tC">{{earnSecd}}</td>
        -->
	</tr>
</script>