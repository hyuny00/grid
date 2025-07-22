<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<script>//<![CDATA[
	$( document ).ready(function() {

		$('#menuTypeCd').select2({
		});


		 createMenuTree();


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

	 		$("#menuTree").jstree("refresh_node", $("#menuSeq").val());
  		    $("#menuTree").jstree("open_node", $("#menuSeq").val());
       });



	 	$("#new").on("click",function(e) {
	 		 e.preventDefault();


	 		$("#menuNm").focus();

	 		 var menuTypeCd=$("#menuTypeCd").val();

	 		 if( $("#upMenuSeq").val() =='' || menuTypeCd =='030'  ){
	 			 alert("상위메뉴를 선택하세요");
	 			 return;
	 		 }
	 		$("#new").hide();
	 		$("#save").show();
	 		$("#delete").hide();

	 		$("#upMenuNm").val($("#menuNm").val());
	    	$("#upMenuSeq").val($("#menuSeq").val());


	    	$("#menuSeqView").val("");

	    	if($("#upMenuSeq").val() =='0'){
	    		//$("#menuTypeCd").val("010").attr("selected", "selected");
	    		$("#menuTypeCd").val("010").select2();
	 		 }else{



	 			 if(menuTypeCd=='010'){
	 				//$("#menuTypeCd").val("020").attr("selected", "selected");
	 				$("#menuTypeCd").val("020").select2();
	 			 }

	 			if(menuTypeCd=='020'){
	 				//$("#menuTypeCd").val("030").attr("selected", "selected");
	 				$("#menuTypeCd").val("030").select2();
	 				//$( "#menuUrl" ).prop( "readonly", false );
	 			 }

	 		 }




	    	$("#mode").val("new");

	    	 init();

       });


	 	$("#save").on("click",function(e) {


	 		 e.preventDefault();

	 		if( $("#upMenuNm").val() =='' ){
	 			 alert("상위메뉴를 선택하세요");
	 			 return;
	 		 }

	 		if(!confirm("저장하시겠습니까?")) return;

	 	//	$("#menuOrd").val($('#sortOrd option').length+1);
	 		var params = $("#aform").serialize();



	 		$.ajax({
			    url:'/admin/menu/saveMenu',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){
						if( $("#upMenuSeq").val() == -1){
							 $("#menuTree").jstree(true).refresh();
						}else{
							$("#menuTree").jstree("refresh_node", $("#upMenuSeq").val());
				  		    $("#menuTree").jstree("open_node", $("#upMenuSeq").val());
						}

			    	}
			    },
			    error: function(err) {
			    }
			});

      });


	 	$("#delete").on("click",function(e) {

	 		e.preventDefault();



	 		if($("#menuSeq").val()==''){
	 			alert("삭제할 항목을 선택하세요.");
	 			return;

	 		}

	 		if($("#upMenuSeq").val()==-1){
	 			alert("최상위 항목을 삭제할수 없습니다.");
	 			return;
	 		}

	 		if(!confirm("삭제하시겠습니까?")) return;

	 		var params = $("#aform").serialize();

	 		$.ajax({
			    url:'/admin/menu/deleteMenu',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){

		    		  	$("#menuTree").jstree("refresh_node", $("#upMenuSeq").val());
			  		    $("#menuTree").jstree("open_node", $("#upMenuSeq").val());
			    	}else{
			    		alert(data.msg);
			    		return;
			    	}
			    },
			    error: function(err) {


			    }
			});


      });


	 	$("#menuTypeCd").change(function() {

	 		 var selected = $('#menuTypeCd').val();


	 		var selectedMenuTypeCd = $("#selectedMenuTypeCd").val();

	 		if(selectedMenuTypeCd =='010' && selected=='030'){
	 			alert("변경할수 없습니다");
	 			return;
	 		}

	 		if(selectedMenuTypeCd =='010' && selected=='010'){
	 			alert("변경할수 없습니다");
	 			return;
	 		}


	 		 if(selected =='010' ){
	 			//$( "#menuUrl" ).val("");
	 			//$( "#menuUrl" ).prop( "readonly", true );
	 		 }
			 if(selected =='020' || selected =='030'){
				 $( "#menuUrl" ).prop( "readonly", false );
	 		 }

			 /*
	 		 if(selected !='010' ){
	 			 if($("#upMenuSeq").val()==0){
	 				alert("상위메뉴를 선택하세요");
	 				$("#menuTypeCd").val("010").attr("selected", "selected");
	 				return;
	 			 }
	 		 }
	 		*/

	 	});

	});


	function updateOrd(){


		var menuOrd = [];
		$('#sortOrd option').each(function(){
			menuOrd.push($(this).val());
		});



		$.ajax({
		    url:'/admin/menu/updateMenuOrd',
		    type:'post',
		    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		    data:{menu_seqs : menuOrd},
		    success: function(data) {
		    	/*
		    	if(data.isSuccess){
	    		  	$("#menuTree").jstree("refresh_node", $("#menuSeq").val());
		  		    $("#menuTree").jstree("open_node", $("#menuSeq").val());
		    	}
		    	*/
		    },
		    error: function(err) {
		    }
		});

	}



	function createMenuTree() {
        $('#menuTree').jstree({
        	 'core': {

        	      'data': {
	        	        "url" :  "/admin/menu/getMenuList/?lazy",


	       	        	 "data": function (node) {
	       	        	 		 return { "menuSeq" : node.id, "useYn":"Y"};
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
        $('#menuTree').on("changed.jstree", function (e, data) {
        });

        $('#menuTree').on('open_node.jstree',function (e, data) {
        	showChildren(data)
        });


        $('#menuTree').on("select_node.jstree", function (e, data) {
        	showChildren(data)
        });


   	 }




	function showChildren(data) {

		$("#new").show();
		$("#save").show();
		$("#delete").show();
		$("#mode").val("update");

		$("#menuNm").val(data.node.text);
    	$("#menuUrl").val(data.node.original.menuUrl);
    	$("#upMenuNm").val(data.node.original.upMenuNm);
    	$("#upMenuSeq").val(data.node.original.upMenuSeq);
    	$("#menuSeq").val(data.node.original.id);
    	$("#menuSeqView").val(data.node.original.id);



    	$("#etc").val(data.node.original.etc);

    //	$("#useYn").val(data.node.original.useYn).attr("selected", "selected");
    	//$("#openYn").val(data.node.original.openYn).attr("selected", "selected");


    	//$("#menuTypeCd").val(data.node.original.menuTypeCd).attr("selected", "selected");
		$("#menuTypeCd").val(data.node.original.menuTypeCd).select2();

    	$("#selectedMenuTypeCd").val(data.node.original.menuTypeCd);


    	if($("#menuTypeCd").val()=='010'){
    		$("#topMenuSeq").val(data.node.original.id);
    	}
    	if($("#menuTypeCd").val()=='020'){
    		$("#topMenuSeq").val(data.node.original.topMenuSeq);
    	}

    	 if(data.node.original.id =='0'){
    		// $("#save").hide();
    		 $("#delete").hide();
    	 }else{
    		 $("#save").show();
    		 $("#delete").show();
    	 }

    	 var selected = $('#menuTypeCd').val();

 		 if(selected =='010' ){
    		//$( "#menuUrl" ).val("");
 			//$( "#menuUrl" ).prop( "readonly", true );
 		 }
		 if(selected =='020' || selected =='030'){
			 $( "#menuUrl" ).prop( "readonly", false );
 		 }



		var node = $("#menuTree").jstree("get_node",data.node.id);


		var selected = $('#sortOrd option:selected');

    	$('#sortOrd').children('option').remove();

    	for (var i = 0; i < node.children.length; i++) {
    		var temp_node = $("#menuTree").jstree("get_node",node.children[i]);

         	 $('#sortOrd').append($('<option/>', {
                 value: temp_node.original.id,
                 text : temp_node.original.text
             }));
        }

    	$("#sortOrd").val(selected.val());

	}

	function init() {

		$("#menuNm").val('');
    	$("#menuUrl").val('');
    	$("#menuSeq").val(0);

    	$("#etc").val('');

    	//$("#useYn option:eq(0)").attr("selected", "selected");

    	//$("#openYn option:eq(1)").attr("selected", "selected");


	}
	//]]>
</script>

<section id="section" class="section">
	<div class="main-cont-box">
		<h2 class="page-title">
			<span>메뉴관리</span>
		</h2>
		<blockquote class="page-desc">메뉴를 이동하고 관리할 수 있습니다.</blockquote>
		<form name="aform" id="aform">
			<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true" />
			<input type="hidden" name="menuOrd" id="menuOrd">
			<input type="hidden" name="mode" id="mode" value="new">
			<input type="hidden" name="useYn" id="useYn" value="Y">
			<input type="hidden" name="selectedMenuTypeCd" id="selectedMenuTypeCd">
				<div class="popup-conts table-scroll">
					<div class="flex pc">
						<div class="flex-box column">
							<div class="scroll2">
								<div class="tree-box">
									<div id="menuTree"></div>
								</div>
							</div>
						</div>
						<div class="flex-box">
							<div class="mL20">
								<div class="Manager">
									<table class="table border2">
										<colgroup>
											<col style="width: 15%">
											<col style="width: 35%">
											<col style="width: 15%">
											<col>
										</colgroup>

										<tr>
											<th><label for="upMenuNm">상위메뉴명</label></th>
											<td>
												<div class="inputbox">
													<input type="text" class="input" id="upMenuNm" name="upMenuNm" value="" readonly />
												</div>
											</td>
											<th><label for="menuTypeCd">메뉴구분</label></th>
											<td>
												<div class="selectbox" style="width: 300%;">
													<select id="menuTypeCd" name="menuTypeCd" class="select">
														<option value="010">시스템</option>
														<option value="020">폴더</option>
														<option value="030">메뉴</option>
													</select>
												</div>
											</td>
										</tr>
										<tr>
											<th><label for="menuNm">메뉴명<span class="orange">*</span></label></th>
											<td>
												<input type="text" class="input" id="menuNm" name="menuNm" value="" maxlength="100" />
											</td>
											<th><label for="menuSeqView">메뉴일련번호</label></th>
											<td>
												<input type="text" class="input" id="menuSeqView" name="menuSeqView" value="" readonly maxlength="100" />
											</td>
										</tr>
										<tr>
											<th><label for="menuUrl">프로그램경로</label></th>
											<td colspan="3">
												<input type="text" class="input" id="menuUrl" name="menuUrl" value="" maxlength="100" />
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
											<th><label for="etc">비고</label></th>
											<td colspan="3" rowspan="3">
												<textarea name="etc" id="etc" class="textarea"></textarea>
											</td>
										</tr>
									</table>
								</div>
							</div>
						</div>
					</div>
					<div class="list-back1" id="menu_button">
						<ul class="btn-list">
							<li id="new"><a href="#" class="btn-submit-s navy">추가</a></li>
							<li id="save"><a href="#" class="btn-submit-s white">저장</a></li>
							<li id="delete"><a href="#" class="btn-submit-s red btn-close">삭제</a></li>
						</ul>
					</div>
				</div>
		</form>
	</div>
</section>

