<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<script type="text/javascript">


</script>
<style>
    #map {
      width: 100%;
      height: 500px;
    }
  </style>
<section id="section" class="section">
    <div class="main-cont-box">



        <br>
        <h1>샘플그리드 기본.. 템플릿 조건문 사용법, 코드져오는 ajax 추가  , 그리드 에서 코드로 값가져오기, 메뉴 네비게이션 추가 </h1>
       <a href="/sample/gridSample0?menuSeq=148&topMenuSeq=146&upMenuSeq=147"> gridSample0 </a>
        <br> <br> <br>

          <h1>샘플그리드 : 삭제, 저장기능</h1>
       <a href="/sample/gridSample1?menuSeq=149&topMenuSeq=146&upMenuSeq=147"> gridSample1 </a>
        <br> <br> <br>
        <h1>샘플그리드   -트리구조 전체조회, 삭제, 저장기능 </h1>
       <a href="/sample/gridSample2?menuSeq=150&topMenuSeq=146&upMenuSeq=147"> gridSample2 </a>
        <br> <br> <br>
         <h1>샘플그리드   -트리구조 부모클릭시 조회, 삭제, 저장기능,  select 코드져오는 ajax 추가  ,그리드에서 셀렉트박스 동적생성 </h1>
       <a href="/sample/gridSample3?menuSeq=151&topMenuSeq=146&upMenuSeq=147"> gridSample3 </a>



       <br> <br> <br>
         <h1>엑셀업로드(C:\ODA_DEV\eGovFrameDev-3.9.0-64bit\workspace\odabogo\WebContent\WEB-INF\jsp\sample\엑셀업로드테스트용.xlsx)</h1>
       <a href="/sample/gridSample5?menuSeq=153&topMenuSeq=146&upMenuSeq=147"> 엑셀업로드 </a>


       <br> <br> <br>
         <h1>기존샘플, 팝업,  파일업로드, 다운로드기능, zip 업도르후 압축 해제, 목록 번호메개기</h1>
       <a href="/sample/selectSampleListForm">기존샘플 </a>


        <br> <br> <br>
         <h1>검색조건과 그리드 통합  , 동적코드가져오기, 그리드 콤보그리기(gridSample3참조).</h1>
       <a href="/sample/gridSample6?menuSeq=154&topMenuSeq=146&upMenuSeq=147">gridSample6 </a>


         <br> <br> <br>


    </div>





    <br> <br> <br>

     <h1>그리드 사용법</h1>

    	<pre><code>
//그리드 초기화시 추가할데이터세팅필요시
var gridData ={id:'123', name : 'qwe'};

  const gridInstance = initTreeGrid({
							 gridId: 'grid1',
							 searchFormId: 'searchForm1',  //검색조건 FormId
							 templateId: 'node-row-template-1',  //그리드 템플릿
							 addRowPosition: 'top',      // 행 추가 위치 (option) 디폴트 top
							 addChildPosition: 'bottom', // 자식행 추가 위치(option) 디폴트 bottom
							 urlParamKey: 'no', // 목록의 key값설정 ,
							 urls: {
								 mainUrl: '/sample/grid/parent',  //초기 url
								 childrenUrl: '/sample/grid/children/{no}', // 트리구조사용시. 부모클릭시 자식 불러올때, lazy(클릭시 로딩)(option)
								 saveUrl: '/sample/grid/save', //(option) 저장url
								 deleteUrl: '/sample/grid/delete' //(option)삭제url 사용시 deleteUrl로 삭제. 사용하지않으면 저장시 삭제정보 서버로 전송
							 },
							 defaultFields: {  //(option) 초기값 지정 필요시
								 name: '',
								 date: ''
							 },

							 gridData: gridData, //(option) 초기로딩시 별도조건 필요할경우
							 pageSize: 5,  //디폴트 10(option)
							 checkCount:1, //체크박스 선택제한  갯수
							 isLoading: 'N',  //(option)디폴트 'Y'  그리드 자동로딩 :Y. 아니면 N
							 treeLoadMode:  'full',   //(option) 'full : 트리 전체로딩' 또는 'lazy(클릭시 로딩)'

							 onRowClick: function(rowData, $row) {
								 console.log('선택된 행:', rowData);
							 },
			                onRowDoubleClick: function(rowData, $row) {
			                    console.log('더블클릭된 행:', rowData);
			                    // 여기에 더블클릭 시 실행할 로직 추가
			                    // 예: 상세 페이지 이동, 수정 모달 열기 등
			                }
						 });


 ////그리드 호출시 추가할데이터세팅필요시
 gridInstance.setGridData(gridData);

 //별도 조회시
 gridInstance.searchData();


 //그리드초기화시    saveUrl 추가하지않고 별도저장시
 function save(gridId){

   var tempGrid = gridManagers[gridId];

	var nodesToUpdate= tempGrid.getModifiedRows();
	var nodesToDelete= tempGrid.getDeleteedRows();

	 const payload = {

          updatedNodes: nodesToUpdate,
          deleteNodes: nodesToDelete

      };

      console.log(updatedNodes);


        $.ajax({
        	url: ....,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: (res) => {

               	toastr["success"]("저장 성공");

                tempGrid.modifiedRows.clear();
                tempGrid.fetchData(tempGrid.currentPage);
            },
            error: (xhr, status, error) => {
                toastr["error"]("저장에 실패했습니다.");
            }
        });


}



 // 외부에서 데이터 설정
 const responseData = {
    total: 100,
    data: [
        { id: 1, name: '테스트1', childYn: 'Y' },
        { id: 2, name: '테스트2', childYn: 'N' },
        // ... 더 많은 데이터
    ]
};

 var tempGrid = gridManagers['gridId'];
 tempGrid.setData(responseData, 1);



  <h1>포맷 </h1>


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





////////////////
셀병합
const treeGrid = new TreeGridManager({

    gridId: 'my-grid',
    mergeCells: {
        'period': true,      // period 병합 - 첫 번째 행에만 체크박스 표시
        'department': true,
        'status': true
    }
});

// data-field="period" 이거 없어도 됨
<td data-field="period">
            {{#if mergeFirst period}}
              <div class="tblChk">  <input type="checkbox"  {{checkedAttr}}  id="chk-{{id}}"  class="row-check"><label for="chk-{{id}}" ></label> </div>
            {{/if}}
 </td>



	</code></pre>
</section>





