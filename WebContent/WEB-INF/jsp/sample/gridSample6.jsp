<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>




<script type="text/javascript">

let initializedData = null;
$(document).ready(function() {

    // 공통 그리드 매니저 인스턴스 생성
    const gridManager = new CommonGridManager();

    // 개별 페이지별 설정
    const pageConfig = {
        // 동적 카테고리 설정
        dynamicCategories: ['schNtnCd', 'schBizFldCd','schAidTpCd','schInstCd'],

        // 각 카테고리별 코드 그룹 매핑
        categoryCodeMapping: {
            schNtnCd: '16',
            schAidTpCd :'21',
            schBizFldCd: 'bizFldCd',  //4단계사업분야 고정값
            schInstCd: 'instCd'//기관코드고정값
        },

        // 2단계이상코드의 첫단계 타이틀
        categoryTitles: {
            'schNtnCd': '대륙',
            'schBizFldCd': '사업분야'
        },

        // 2단계이상 필터가 필요한 카테고리들을 정의
        multiStepCategories: ['schNtnCd','schBizFldCd'],



        // 그리드 설정들
        gridConfigs: [
            {
                gridId: 'grid1',
                searchFormId: 'searchForm',
                templateId: 'node-row-template-1',
                urls: {
                    mainUrl: '/sample/newSampleList2',
                },
                pageSize: 10,
                onRowClick: function(rowData, $row,event) {

                	// event가 전달되지 않을 수도 있으므로 안전하게 체크
                    if (event && $(event.target).closest('.tblChk').length > 0) {
                        return false;
                    }


                    console.log('선택된 행:', rowData);
                },
                onRowDoubleClick: function(rowData, $row) {
                    console.log('더블클릭된 행:', rowData);
                    // 여기에 더블클릭 시 실행할 로직 추가
                    // 예: 상세 페이지 이동, 수정 모달 열기 등
                },
                // 병합할 필드들 설정
                mergeCells: {

                    'statusCd': true       // status 필드 병합
                }
            },
            {
                gridId: 'grid2',
                searchFormId: 'searchForm',
                templateId: 'node-row-template-2',
                urls: {
                    mainUrl: '/sample/newSampleList2',
                },
                pageSize: 10,
                onRowClick: function(rowData, $row) {
                    console.log('선택된 행:', rowData);
                },
                onRowDoubleClick: function(rowData, $row) {
                    console.log('더블클릭된 행:', rowData);
                    // 여기에 더블클릭 시 실행할 로직 추가
                    // 예: 상세 페이지 이동, 수정 모달 열기 등
                }
            }
        ],

        // 다중 코드 요청 설정 (선택사항 - 필요 없으면 주석 처리 또는 삭제)
        codeRequests: [
            {schCodeDiv:'fff', code: '', cdGroupSn: '1' },
            {schCodeDiv:'ntnCd', code: '', cdGroupSn: '' }
        ],

        codeListRequests : [
    	    {schCodeDiv:'statusOptions', code: '', cdGroupSn: '1' },
    	    {schCodeDiv:'statusOptions2', code: '', cdGroupSn: '1' },
    	    {schCodeDiv:'ntnCd', code: '', cdGroupSn: '' }
    	]

        // 다중 코드가 필요 없는 경우 아래와 같이 빈 배열이나 null로 설정
        // codeRequests: null  // 또는 []
    };




    // 모든 초기화 작업 시작
    const result = gridManager.initializeAllData(pageConfig)
        .then(result => {
            console.log('초기화 완료:', result);

            // 전역 변수에 저장
            initializedData = result;
            filterSystem = result.filterSystem;
            gridInstances = result.gridInstances;

            // 초기화 완료 후 초기 필터 설정필요시
            setupInitialFilters();

        })
        .catch(error => {
            console.error('초기화 실패:', error);
        });



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
    filterSystem.setSimpleInitialFilters({
        "schNtnCd": { value: "229", text: "시행기관 229" },
        "period": { value: "2024-01-01,2024-01-03", text: "시작일: 2024-01-01 - 2024-01-03" }
    });

    console.log('초기 필터 설정 완료');
}


function someOtherFunction() {
	//  var a=getGridById('grid1');

	// a.setPageSize(5);




	 const gridManager = gridManagers['grid1'];


	 // 1. 인덱스로 행 데이터 가져오기 (0부터 시작)
	 const firstRowData = gridManager.getRowDataByIndex(0);



	// 3. 인덱스로 특정 필드만 업데이트
	// gridManager.updateRowFieldByIndex(1, 'projectId', '1111');


	 gridManager.updateRowDataByIndex(3, {
		   projectId: '첫 번째 행 수정',
		   projectTitle: '2222'
		});

	// const thirdRowData = gridManager.getRowDataByIndex(2);




}
</script>

<div class="pgtBox">
					<div class="lt">
						<h2><a href="javascript:someOtherFunction()">요청 중인 연계</a></h2>
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

				<div class="schBox">
				    <div class="searchTxt">
				        <p>사업명</p>
						<div>
						    <input type="text" placeholder="사업명을 입력하세요." id="projectNameInput">
							<button type="button" class="btn-src" id="textSearchBtn">검색</button>
						</div>
						<button type="button" class="btn-flt" id="filterToggleBtn">검색 필터 열기</button>
				    </div>

				    <div class="searchFlt" id="searchFilterArea">
				        <div class="fltList">
				            <ul>
				                <li><button type="button" class="filter-category-btn" data-category="schPeriod">사업기간</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="schInstCd">시행기관</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="schNtnCd">수원국</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="schBizFldCd">사업분야</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="status">진행상태</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="schAidTpCd">원조유형</button></li>
				                 <li><button type="button" class="filter-category-btn" data-category="schBudget">사업예산</button></li>
				            </ul>
				        </div>

						<div class="fltCont" id="filterOptions">
						    <ul>
						        <li class="contBox type01">
						            <p id="pTxt"></p>
						            <div class="contList">
						                <div class="first-step-options">
						                    <p></p>
						                    <ul class="first-step-list">
						                        <!-- 1단계 옵션들 -->
						                    </ul>
						                </div>
						                <div class="second-step-options" style="display: none;">
						                    <p>상세 선택</p>
						                    <ul class="second-step-list">
						                        <!-- 2단계 옵션들 -->
						                    </ul>
						                </div>
						                <!-- 3단계 이상 추가 -->
					    <div class="third-step-options" style="display: none;">
					        <p>세부 선택</p>
					        <ul class="third-step-list"></ul>
					    </div>
					    <div class="fourth-step-options" style="display: none;">
					        <p>최종 선택</p>
					        <ul class="fourth-step-list"></ul>
					    </div>

					    <div class="fifth-step-options" style="display: none;">
						    <p>최종 세부 선택</p>
						    <ul class="fifth-step-list"></ul>
						</div>
						            </div>


									<!-- 1단계 필터 전용 (기존 구조 유지) -->
									<div class="filter-options-content" style="display: none;">
									    <!-- 1단계 필터 내용 -->
									</div>

						        </li>
						    </ul>
						</div>

				        <div class="fltOpt">
                            <ul id="selectedFilters">
                                <li class="empty-state">
                                    <p>필터를 선택하면 여기에 표시됩니다</p>
                                </li>
                            </ul>
				            <button type="reset" class="btn-reset txt" id="resetFiltersBtn">초기화</button>
				        </div>
				    </div>

				    <button type="button" class="btn-apply" id="applyFiltersBtn">조건검색</button>
				</div>
				</form>
				<div id="grid1-container">
		<div class="schList">
		    <div class="titBox">
		        <div class="lt"></div>
		        <div class="rt">
		            <div class="btn-wrap">
		                <button type="button" class="btn excel">엑셀다운로드</button>
		                <button type="button" class="btn hwp">한글다운로드</button>
		                <button type="button" class="btn pdf">PDF다운로드</button>
		                <button type="button" class="btn biz">사업등록</button>
		            </div>
		        </div>
		    </div>

		    <div class="tblBox">
				<div class="resize-indicator" id="resize-indicator"></div>
		        <table class="tbl col resizable-table" id="grid1">
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
		                        <div class="resizer"></div>
		                    </th>
		                    <th scope="col">사업번호<div class="resizer"></div></th>
		                    <th scope="col">사업명<div class="resizer"></div></th>
		                    <th scope="col">사업기간<div class="resizer"></div></th>
		                    <th scope="col">시행기관<div class="resizer"></div></th>
		                    <th scope="col">수원국<div class="resizer"></div></th>
		                    <th scope="col">사업분야<div class="resizer"></div></th>
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
		    <div id="grid2-container">

		     <div class="tblBox">

		        <table class="tbl col" id="grid2">
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
		                            <input type="checkbox" id="chk02" class="check-all"><label for="chk02"></label>
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
		            <tbody id="grid2-body">
		            </tbody>
		        </table>


			        <!-- pagination -->
			        <div class="pagination" id="grid2-pagination">

			        </div>
			        <!-- //pagination -->

		    </div>
		</div>


<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">



<tr>

<td>
            {{#if mergeFirst statusCd}}
              <div class="tblChk">  <input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check" ><label for="chk-{{id}}" ></label> </div>
            {{/if}}
 </td>

   <td class="tC">

{{#if '${testData}' equals '1234' or 'B' equals 'A'  }}
  레벨 1 카테고리 A 항목입니다.

{{else}}
{{projectId}}
{{/if}}





</td>
    <td>
        <a href="#"><span class="span orange">{{projectType}}</span>{{projectTitle}}</a>
    </td>
    <td class="tC">{{period}}</td>
    <td>{{department}}</td>
    <td>{{country}} <select class="form-control form-control-sm" data-field="statusCd" data-value="{{statusCd}}" readonly>
        <option value="">선택하세요</option>
        {{#each statusOptions}}
        <option value="{{this.value}}" {{#if this.value equals statusCd}}selected{{/if}}>{{this.text}}</option>
        {{/each}}
    </select></td>
    <td>{{sector}}{{ntnCd['192']}}</td>

    <td>
        <button type="button" class="btn btn-sm btn-edit-row" data-row-id="{{id}}">수정</button>
    </td>
</tr>


</script>


<script type="text/html" id="node-row-template-2">

<tr>
    <td>
        <div class="tblChk">
            <input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}"></label>
        </div>
    </td>
    <td class="tC">{{projectId}}</td>
    <td>
        <a href="#"><span class="span orange">{{projectType}}</span>{{projectTitle}}</a>
    </td>
    <td class="tC">{{period}}</td>
    <td>{{department}}</td>
    <td>{{country}}         </td>
    <td>{{sector}}</td>
    <td><span class="badge pt">{{status}}   </span></td>
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

// 수정/저장 버튼 이벤트 처리
$(document).on('click', '.btn-edit-row', function() {
    const $btn = $(this);
    const $row = $btn.closest('tr');
    const rowId = $btn.data('row-id');

    // 현재 버튼 상태 확인
    const isEditing = $btn.text().trim() === '저장';

    if (!isEditing) {
        // "수정" 상태 -> 편집 모드로 변경
        // input과 select 요소 모두 처리
        $row.find('input[data-field], select[data-field]').each(function() {
            $(this).prop('readonly', false).prop('disabled', false);
        });
        $row.find('input[data-field]:first').focus();
        $btn.text('저장').removeClass('btn-edit-row').addClass('btn-save-row');
    } else {
        // "저장" 상태 -> 저장 후 읽기 전용으로 변경
        const formData = {};
        $row.find('input[data-field], select[data-field]').each(function() {
            const $input = $(this);
            const fieldName = $input.data('field');
            formData[fieldName] = $input.val();
        });

        // 읽기 전용으로 변경 (input과 select 모두)
        $row.find('input[data-field], select[data-field]').each(function() {
            $(this).prop('readonly', true).prop('disabled', true);
        });

        $btn.text('수정').removeClass('btn-save-row').addClass('btn-edit-row');

        // 서버에 저장 (필요시)
        console.log('저장할 데이터:', { id: rowId, ...formData });
        // AJAX를 통해 서버에 저장하는 로직을 여기에 추가할 수 있습니다
        // $.ajax({
        //     type: 'POST',
        //     url: '/sample/grid/update',
        //     data: JSON.stringify({ id: rowId, ...formData }),
        //     contentType: 'application/json',
        //     success: function(response) {
        //         console.log('저장 완료');
        //     }
        // });
    }
});
</script>
