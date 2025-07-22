<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<script type="text/javascript">
$(document).ready(function() {

	
	// Ajax로 카테고리 데이터를 가져오는 함수
	function loadCategoryData(category) {
	    return new Promise((resolve, reject) => {
	        const cdGroupSn = categoryCodeMapping[category];
	        
	        $.ajax({
	            url: '/common/selectCode',
	            type: 'get',
	            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
	            data: { 
	                cdGroupSn: cdGroupSn 
	            },
	            success: function(data) {
	                // 받은 데이터를 categoryData 구조로 변환
	                const transformedData = data.map(item => ({
	                    value: item.code,  // 또는 item.value (실제 응답 구조에 따라)
	                    text: item.text
	                }));
	                resolve(transformedData);
	                
	                console.log("transformedData.."+JSON.stringify(transformedData));
	            },
	            error: function(xhr, status, error) {
	                console.error("Failed to loaddata:");
	                reject(error);
	            }
	        });
	    });
	}
	

	// 모든 동적 카테고리 데이터를 로드하는 함수
	async function initializeCategoryData() {
	    const categoryData = {
	    		 'period': [
	    		        { 
	    		            value: 'period_range', 
	    		            text: '',
	    		            type: 'date'
	    		        }
	    		    ],
	    		    
	    		    'budget': [
	    		        { 
	    		            value: 'current_year_budget', 
	    		            text: '',
	    		            type: 'budget' 
	    		        }
	    		    ]
	    };

	    // 동적 카테고리들을 순차적으로 로드
	    for (const category of dynamicCategories) {
	        try {
	            categoryData[category] = await loadCategoryData(category);
	            console.log("data loaded successfully");
	        } catch (error) {
	            console.error("Failed to load");
	            // 실패 시 빈 배열로 초기화
	            categoryData[category] = [];
	        }
	    }

	    return categoryData;
	}
	
	
	
	  
	const dynamicCategories = ['schNtnCd', 'schBizTpCd'];

	// 각 카테고리별 코드 그룹 매핑 (실제 값에 맞게 수정 필요)
	const categoryCodeMapping = {
			schNtnCd: '16',
			schBizTpCd: '3'
	   
	};
	
	const categoryTitles = {
		    'schNtnCd': '대륙',
	};
	  // 2단계 필터가 필요한 카테고리들을 정의
  	const multiStepCategories = ['schNtnCd'];
	
	
	let categoryData;
	initializeCategoryData().then(categoryData => {
	    console.log('All category data loaded:', categoryData);
	    
	    const gridInstance1 = initTreeGrid({
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
	    
	    const gridInstance2 = initTreeGrid({
		     gridId: 'grid2',
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
	    
	    
		//검색조건으로 그리드 2개이상 검색시		
		 const filterSystem = new ODAFilterSystem(categoryData, [gridInstance1,gridInstance2],multiStepCategories,categoryTitles);
		
		//검색조건으로 그리드  검색시		
		 //const filterSystem = new ODAFilterSystem(categoryData, gridInstance1 ,multiStepCategories,categoryTitles);
		
		
		 window.odaFilterSystem = filterSystem;


	});
	
	
	//console.log("..."+categoryData);
	
	/*
	const categoryData = {
		period: [
			{ value: 'start-date', text: '시작일 선택', type: 'date' },
			{ value: 'end-date', text: '종료일 선택', type: 'date' },
			{ value: '2023', text: '2023년' },
			{ value: '2024', text: '2024년' },
			{ value: '2025', text: '2025년' }
		],
		agency: [
			{ value: 'koica', text: 'KOICA' },
			{ value: 'moe', text: '교육부' },
			{ value: 'nrf', text: '국무조정실' },
			{ value: 'mof', text: '기획재정부' },
			{ value: 'mofa', text: '외교부' }
		],
		country: [
			{ value: 'bangladesh', text: '방글라데시' },
			{ value: 'vietnam', text: '베트남' },
			{ value: 'cambodia', text: '캄보디아' },
			{ value: 'laos', text: '라오스' },
			{ value: 'mongolia', text: '몽골' },
			{ value: 'nepal', text: '네팔' },
			{ value: 'ghana', text: '가나' },
			{ value: 'rwanda', text: '르완다' }
		],
		field: [
			{ value: 'education', text: '교육' },
			{ value: 'health', text: '보건' },
			{ value: 'agriculture', text: '농업' },
			{ value: 'environment', text: '환경' },
			{ value: 'governance', text: '거버넌스' },
			{ value: 'infrastructure', text: '인프라' },
			{ value: 'public_admin', text: '일반 공공행정 및 시민사회' }
		],
		status: [
			{ value: 'confirmed', text: '확정' },
			{ value: 'planning', text: '계획' },
			{ value: 'ongoing', text: '진행중' },
			{ value: 'completed', text: '완료' },
			{ value: 'suspended', text: '중단' }
		],
		aid_type: [
			{ value: 'grant', text: '무상원조' },
			{ value: 'loan', text: '유상원조' },
			{ value: 'technical', text: '기술협력' },
			{ value: 'scholarship', text: '장학사업' },
			{ value: 'training', text: '연수사업' },
			{ value: 'package', text: '패키지사업' }
		]
	};
	*/
	
	/*
	 // 첫 번째 그리드 초기화
	 const gridInstance = initTreeGrid({
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

	
	 const filterSystem = new ODAFilterSystem(categoryData, gridInstance);
	 window.odaFilterSystem = filterSystem;
	*/ 
	 
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
				                <li><button type="button" class="filter-category-btn" data-category="period">사업기간</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="schBizTpCd">시행기관</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="schNtnCd">수원국</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="field">사업분야</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="status">진행상태</button></li>
				                <li><button type="button" class="filter-category-btn" data-category="aid_type">원조유형</button></li>
				                 <li><button type="button" class="filter-category-btn" data-category="budget">사업예산</button></li>
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
	</div>
	
	
<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">
   
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
    <td>{{country}}</td>
    <td>{{sector}}</td>
    <td><span class="badge pt">{{status}}</span></td>
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
    <td>{{country}}</td>
    <td>{{sector}}</td>
    <td><span class="badge pt">{{status}}</span></td>
</tr>


</script>

	
