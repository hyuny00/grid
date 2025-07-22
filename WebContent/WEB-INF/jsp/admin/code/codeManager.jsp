<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>



<script>//<![CDATA[


	$( document ).ready(function() {

		$('#useYn').select2({
		});

		 createCodeTree();


		 $("#up").on("click",function(e) {
			e.preventDefault();
		  	var selected = $('#sortOrd option:selected');

		    if(selected[0].previousElementSibling == null) return;
		    $(selected).each(function(index, obj){
		          $(obj).insertBefore($(obj).prev());
		    });

		    updateOrd();
        });


	 	$("#down").on("click",function(e) {
	 		 e.preventDefault();
 			var selected = $('#sortOrd option:selected');
	       	if(selected.last().next().length == 0) return;
	      	 $(selected.get().reverse()).each(function(index, obj){
				$(obj).insertAfter($(obj).next());
	       	});

	      	updateOrd();
        });


	 	$("#reload").on("click",function(e) {
	 		 e.preventDefault();

	 		$("#codeTree").jstree("refresh_node", $("#cdSeq").val());
  		    $("#codeTree").jstree("open_node", $("#cdSeq").val());
        });


	 	$("#new").on("click",function(e) {
	 		 e.preventDefault();


	 		$("#dtlCdNm").focus();


	 		 if(  $("#upCdSeq").val() ==''){
	 			 alert("상위메뉴를 선택하세요");
	 			 return;
	 		 }

	 		$("#new").hide();
	 		$("#save").show();
	 		$("#delete").hide();

	 		$("#upDtlCdNm").val($("#dtlCdNm").val());
	    	$("#upCdSeq").val($("#cdSeq").val());


	    	$("#upCdDiv").val($("#cdDiv").val());
	    	$("#cdDiv").val("");



	    	$("#mode").val("new");

	    	 init();

       });


	 	$("#save").on("click",function(e) {


	 		 e.preventDefault();

	 		if($("#upCdSeq").val() =='0'){
	 			alert("상위코드를 선택하세요");
	 			return;
	 		}

	 		if($("#dtlCd") ==''){
	 			alert("코드를 입력하세요");
	 			return;
	 		}

	 		if($("#dtlCdNm") ==''){
	 			alert("코드명을 입력하세요");
	 			return;
	 		}


	 		if(!confirm("저장하시겠습니까?")) return;

	 		var params = $("#aform").serialize();


	 		$.ajax({
			    url:'/admin/code/saveCode',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){

		    		  	$("#codeTree").jstree("refresh_node", $("#upCdSeq").val());
			  		    $("#codeTree").jstree("open_node", $("#upCdSeq").val());
			    	}
			    },
			    error: function(err) {
			    }
			});

      });


	 	$("#delete").on("click",function(e) {

	 		e.preventDefault();


	 		if($("#cdSeq").val()==''){
	 			alert("삭제할 항목을 선택하세요.");
	 			return;

	 		}

	 		if($("#upCdSeq").val()=='0'){
	 			alert("최상위 항목을 삭제할수 없습니다.");
	 			return;
	 		}

	 		if(!confirm("삭제하시겠습니까?")) return;

	 		var params = $("#aform").serialize();

	 		$.ajax({
			    url:'/admin/code/deleteCode',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){

		    		  	$("#codeTree").jstree("refresh_node", $("#upCdSeq").val());
			  		    $("#codeTree").jstree("open_node", $("#upCdSeq").val());
			    	}else{
			    		alert(data.msg);
			    		return;
			    	}
			    },
			    error: function(err) {


			    }
			});


      });



	});


	function updateOrd(){


		var codeOrd = [];
		$('#sortOrd option').each(function(){
			codeOrd.push($(this).val());
		});



		$.ajax({
		    url:'/admin/code/updateCodeOrd',
		    type:'post',
		    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		    data:{code_seqs : codeOrd},
		    success: function(data) {
		    	/*
		    	if(data.isSuccess){
	    		  	$("#codeTree").jstree("refresh_node", $("#cdSeq").val());
		  		    $("#codeTree").jstree("open_node", $("#cdSeq").val());
		    	}
		    	*/
		    },
		    error: function(err) {
		    }
		});

	}



	function createCodeTree() {
        $('#codeTree').jstree({
        	 'core': {

        	      'data': {
	        	        "url" :  "/admin/code/getCodeList/?lazy",


	       	        	 "data": function (node) {
	       	        	 		 return { "cdSeq" : node.id, "useYn":"Y"};
	                      }
        	      } ,
        	      "check_callback" : true,
        	    },

  				"types" : {

    	    	    "last" : {
    	    	      "icon" : "/images/filemanager/icon_doc.gif",
    	    	      "valid_children" : []
    	    	    }

    	    	  },

        	    "plugins" : [
        	    	"types"
        	      ]

        });


        // Listen for events - example

        $('#codeTree').on("changed.jstree", function (e, data) {
        });

        $('#codeTree').on('open_node.jstree',function (e, data) {

        	showChildren(data)
        });


        $('#codeTree').on("select_node.jstree", function (e, data) {



        	showChildren(data)
        });


   	 }




	function showChildren(data) {

		$("#new").show();

		$("#save").show();
		$("#delete").show();
		$("#mode").val("update");

		$("#dtlCdNm").val(data.node.original.dtlCdNm);

    	$("#upDtlCdNm").val(data.node.original.upDtlCdNm);

    	$("#shotDtlCdNm").val(data.node.original.shotDtlCdNm);


    	$("#upCdSeq").val(data.node.original.upCdSeq);
    	$("#cdSeq").val(data.node.original.id);

    	$("#upCdDiv").val(data.node.original.upCdDiv);
    	$("#cdDiv").val(data.node.original.cdDiv);
    	$("#dtlCd").val(data.node.original.dtlCd);



    	$("#cdRmk").val(data.node.original.rmk);

    	//$("#useYn").val(data.node.original.useYn).attr("selected", "selected");

    	$("#useYn").val(data.node.original.useYn).select2();

    	//$("#openYn").val(data.node.original.openYn).attr("selected", "selected");



    	 if(data.node.original.id =='0'){
    		 $("#save").hide();
    		 $("#delete").hide();
    	 }else{
    		 $("#save").show();
    		 $("#delete").show();
    	 }


		var node = $("#codeTree").jstree("get_node",data.node.id);

		var selected = $('#sortOrd option:selected');
    	$('#sortOrd').children('option').remove();
    	for (var i = 0; i < node.children.length; i++) {

    		var temp_node = $("#codeTree").jstree("get_node",node.children[i]);

         	 $('#sortOrd').append($('<option/>', {
                 value: temp_node.original.id,
                 text : temp_node.original.text
             }));
        }

    	$("#sortOrd").val(selected.val());
	}

	function init() {

		$("#dtlCdNm").val('');
    	$("#cdSeq").val(0);



    	$("#dtlCd").val('');
    	$("#shotDtlCdNm").val('');

    	$("#cdRmk").val('');

    //	$("#useYn option:eq(0)").attr("selected", "selected");

	}





	//]]>
</script>
<section id="section" class="section">
	<div class="main-cont-box">
		<form name="aform" id="aform">
			<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true" />
			<input type="hidden" name="codeOrd" id="codeOrd">
			<input type="hidden" name="mode" id="mode" value="new">
			<h2 class="page-title">
				<span>코드관리</span>
			</h2>
			<blockquote class="page-desc">코드를 관리할 수 있습니다.</blockquote>
			<div class="">
				<div class="popup-conts table-scroll">
					<div class="flex pc">
						<div class="flex-box3 column">
							<div class="scroll3">
								<div class="tree-box" id="codeTree"></div>
							</div>
						</div>
						<div class="flex-box">
							<div class="scroll3 mL20" id="codeDetail">
								<table class="table border2">
									<colgroup>
										<col style="width: 15%;"/>
										<col style="width: 35%;"/>
										<col style="width: 15%;"/>
										<col style="width: 35%;"/>
									</colgroup>
									<tr>
										<th><label for="upDtlCdNm">상위코드명</label></th>
										<td>
											<div class="inputbox">
												<input type="text" class="input" id="upDtlCdNm" name="upDtlCdNm" value="" readonly>
											</div>
										</td>
										<th><label for="upCdSeq">상위코드 일련번호</label></th>
										<td>
											<input type="text" class="input" id="upCdSeq" name="upCdSeq" value="" readonly>
										</td>
									</tr>
									<tr>

										<th><label for="dtlCd">코드</label></th>
										<td>
											<input type="text" class="input" id="dtlCd" name="dtlCd" value="" maxlength="3">
										</td>

										<th><label for="cdSeq">코드일련번호</label></th>
										<td>
											<input type="text" class="input" id="cdSeq" name="cdSeq" value="" readonly>
										</td>
									</tr>
									<tr>
										<th><label for="dtlCdNm">코드명</label></th>
										<td>
											<input type="text" class="input" id="dtlCdNm" name="dtlCdNm" value="" maxlength="50">
										</td>
										<th><label for="shotDtlCdNm">단축 코드명</label></th>
										<td>
											<input type="text" class="input" id="shotDtlCdNm" name="shotDtlCdNm" value="" maxlength="25" placeholder="최대 25글자">
										</td>
									</tr>
									<tr>
										<th><label for="upCdDiv">이전 상위 코드</label></th>
										<td>
											<input type="text" class="input" id="upCdDiv" name="upCdDiv" value="" readonly>
										</td>
										<th><label for="dtlCd">이전 코드</label></th>
										<td>
											<input type="text" class="input" id="cdDiv" name="cdDiv" value="" readonly>
										</td>
									</tr>
									<tr>
										<th><label for="shotDtlCdNm">사용여부</label></th>
										<td colspan="3">
											<div class="selectbox">
												<select id="useYn" name="useYn" class="select-s">
													<option value="Y">Yes</option>
													<option value="N">No</option>
												</select>
											</div>
										</td>
									</tr>
									<tr>
										<th><label for="sortOrd">정렬순서</label></th>
										<td colspan="3">
											<div class="filexbox">
												<div class="btn-add-delete">
													<button type="button" class="arr" id="up">
														<i class="arr-up"></i>
													</button>
													<button type="button" class="arr" id="down">
														<i class="arr-down"></i>
													</button>

													<button type="button" class="arr" id="reload" title="트리 리로드">
														<i class="fas fa-sync"></i>
													</button>
												</div>
												<ul id="sortList" class="textarea">
													<li style="width: 100%; height: 100%;"><select id="sortOrd" name="sortOrd" multiple style="width: 100%; height: 100%;">
														</select></li>
												</ul>
											</div>
										</td>
									</tr>
									<tr>
										<th><label for="cdRmk">비고</label></th>
										<td colspan="3">
											<textarea name="cdRmk" id="cdRmk" class="textarea"></textarea>
										</td>
									</tr>

								</table>
							</div>
						</div>
					</div>
					<div class="list-back4">
						<ul class="btn-list">
							<li id="new"><a href="#" class="btn-submit-s navy">추가</a></li>
							<li id="save"><a href="#" class="btn-submit-s white">저장</a></li>
							<li id="delete"><a href="#" class="btn-submit-s red btn-close">삭제</a></li>
						</ul>
					</div>
				</div>
			</div>
		</form>
	</div>
	<!-- //end) .main-cont-box -->
</section>
