<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
  <style>
   
    #example-table {
      height: 500px;
    }
    
    
.tree-row { 
    display: none; 
    background: #f9f9f9; 
}
.tree-row.show {
    display: table-row;
}
td[contenteditable="true"] { 
    background-color: #fff8dc; 
    cursor: text;
}
.date-input { 
    width: 120px; 
}
.tree-indent {
    padding-left: 20px;
}
.tree-toggle {
    cursor: pointer;
    margin-right: 8px;
    display: inline-block;
    width: 16px;
    text-align: center;
    color: #007bff;
    font-weight: bold;
}
.tree-toggle:hover {
    color: #0056b3;
}
.tree-toggle.no-children {
    color: transparent;
    cursor: default;
}

.pagination .page-link {
    color: #007bff;
}

.pagination .page-item.disabled .page-link {
    color: #6c757d;
    cursor: not-allowed;
}

.pagination .page-item.active .page-link {
    background-color: #007bff;
    border-color: #007bff;
}

.page-info {
    font-size: 0.875rem;
    color: #6c757d;
}

.grid-container {
    margin-bottom: 30px;
    border: 1px solid #ddd;
    padding: 15px;
    border-radius: 5px;
}

.pagination-btn {
    display: inline-block;
    margin: 0 4px;
    padding: 4px 10px;
    border: 1px solid #ccc;
    background-color: #f9f9f9;
    color: #333;
    cursor: pointer;
    user-select: none;
    border-radius: 3px;
    font-size: 14px;
}

.pagination-btn:hover:not(.disabled):not(.active) {
    background-color: #eee;
}

.pagination-btn.active {
    background-color: #007bff;
    color: white;
    font-weight: bold;
}

.pagination-btn.disabled {
    color: #aaa;
    cursor: default;
    pointer-events: none;
    background-color: #f0f0f0;
}
  </style>
  
    <script type="text/javaScript" >
    

	    $(document).ready(function(){
	    	
	    	 initTreeGrid({
	    	     gridId: 'grid1',
	    	     searchFormId: 'listForm',
	    	     templateId: 'node-row-template-1',
	    	     urls: {
	    	    	 mainUrl: '/sample/selectSampleList',
	    	     },
	    	     pageSize: 3,
	    	     
	    	     onRowClick: function(rowData, $row) {
	    	         console.log('선택된 행:', rowData);
	    	         sampleDetail(rowData.id);
	    	     }
	    	 });
	    	
	    	
	    	// select2 초기화
	    	$('#test').select2(
	    		{width: '20%'}
	    	);


	    	//$('#test2').select2();
	    	
	    	$('#test2').select2({
	    		maximumSelectionLength:2
	    	});

	    	$('#test3').select2();


	    	$("#btn-excel").on("click", function () {
	              var $preparingFileModal = $("#preparing-file-modal");
	              $preparingFileModal.dialog({ modal: true });
	              $("#progressbar").progressbar({value: false});
	              $.fileDownload("/file/largeExcelDown", {

	            	  httpMethod: "post",
            	 	  data:$("#listForm").serialize(),

	                  successCallback: function (url) {
	                      $preparingFileModal.dialog('close');
	                  },
	                  failCallback: function (responseHtml, url) {
	                      $preparingFileModal.dialog('close');
	                      $("#error-modal").dialog({ modal: true });
	                  }
	              });
	              return false;
          	});
	    	


	    });
	    
	    
	 	


        /* 글 수정 화면 function */
        function sampleDetail(id) {
        	document.listForm.id.value = id;
           	document.listForm.action = "${basePath}/sample/selectSample";
           	document.listForm.submit();
        }

        /* 글 등록 화면 function */
        function sampleForm() {
           	document.listForm.action = "${basePath}/sample/sampleForm";
           	document.listForm.submit();
        }

       


        function selectCode( ) {


        	var code= $("#test option:selected").data("code")
        	var cdGroupSn = $("#test option:selected").data('code-group');

	    	$.ajax({
	    		url: '/common/selectCode',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: {"code": code, "cdGroupSn": cdGroupSn},
			    success: function(data) {

			    	$('#test3').empty();

			    	if(data){

			    		data.unshift({id: "", text:"선택"});

			    		$('#test3').select2({
				    		placeholder: '선택',
				    		data: data
				    	});


			    		//$('#test2').select2().next().hide();

			    	}

			    },
			    error: function(err) {
			    }
			});

	    }

    </script>
<section id="section" class="section">
  <div class="main-cont-box">
      <div class="rightcolumn">
    <form  id="listForm" name="listForm"  method="post" action="${basePath}/sample/selectSampleList">
    	<!-- 메뉴, 페이징 파라메터-->
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
        <input type="hidden" name="id" value="">
        	<!-- // 타이틀 -->
        	<div id="search">
        		<ul>
        			<li>
        				<select name="schCondition" id="schCondition" >
        					<option value="1" <c:if test="${param.schCondition eq '1'}">selected </c:if> >Name</option>
        					<option value="0" <c:if test="${param.schCondition eq '0'}">selected </c:if> >ID</option>
        				</select>
        			</li>
        			<li>
                        <input type="text" id="schKeyword"  name="schKeyword" value="${param.schKeyword}" />
                    </li>
        			

        	        <li>
        				<select name="test" id="test"  onchange="selectCode();">
      							<option value="">전체</option>
      							<c:forEach var="code" items="${sampleCodeList}" varStatus="status">
      								<option value="${code.code}" data-code="${code.code}"  data-code-group="${code.cdGroupSn}" > ${code.value}</option>
      							</c:forEach>
        				</select>

        				<select name="test3" id="test3" style="width:300px">
        					<option >전체</option>
        				</select>

        				<select name="test2" id="test2" style="width:300px" multiple="multiple">
        						<option value="01">중소기업</option>
								<option value="02">소상공인</option>
								<option value="03">중앙부처</option>
								<option value="04">지자체</option>
								<option value="05">관행</option>
								<option value="06">현장대기</option>
        				</select>
        			</li>


                </ul>
                
                 <!-- 그리드 액션 버튼들 -->
                  <div class="row mt-3">
                            <div class="col-12">
                                <button type="button" class="btn btn-primary btn-search">
                                    <i class="fa fa-search"></i> 조회
                                </button>
                                  <button type="button" class="btn btn-secondary ml-2 btn-reset">
                                    <i class="fa fa-refresh"></i> 초기화
                                </button>
                            </div>
                        </div>
                <div id="grid1-container">
                    <table class="table table-bordered" id="grid1">
                        <thead class="thead-dark">
                            <tr>
                                <th>ID</th>
                                <th>이름</th>
                                <th>useYn</th>
                                <th>description</th>
                                <th>regUser</th>
                            </tr>
                        </thead>
                        <tbody id="grid1-body"></tbody>
                    </table>
                    
                    <nav>
                        <ul class="pagination" id="grid1-pagination"></ul>
                        <div id="grid1-page-info" class="page-info"></div>
                    </nav>
                </div>
        	</div>
        	
        	

        	</div>
        	<jsp:include page="/WEB-INF/jsp/framework/_includes/paging.jsp" flush="true"/>

        	<div id="sysbtn">
        	  <ul>
        	      <li>
        	          <span class="btn_blue_l">
        	              	<a href="javascript:sampleForm();">create</a>
                      </span>
                      <sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_USER')">
						   [권한 체크]
					  </sec:authorize>

					  <button id="btn-excel" >[엑셀 다운로드]</button>


                  </li>
              </ul>
        	</div>
    </form>
		<table><tr><td>
		<div id="appendLayer" > </div>
		</td></tr></table>
	</div>

</div>
</section>

<!-- 첫 번째 그리드용 템플릿 -->
<script type="text/html" id="node-row-template-1">
    <tr >
 		<td>{{id}}</td>
        <td>
            <span>{{name}}</span>
        </td>
		<td>
        	{{useUn}}
        </td>
        <td>
        	{{description}}
        </td>
 		<td>
        	{{regUser}}
        </td>
    </tr>
</script>
