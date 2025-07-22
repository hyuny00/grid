<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<script>//<![CDATA[

	$( document ).ready(function() {


		$('#roleTypeCd').select2({
		});
		$('#useYn').select2({
		});


		createRoleTree();

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

		function updateOrd(){
			var roleOrd = [];
			$('#sortOrd option').each(function(){
				roleOrd.push($(this).val());
			});

			$.ajax({
			    url:'/admin/auth/updateAuthMenuOrd',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data:{role_seqs : roleOrd},
			    success: function(data) {
			    }
			    ,error: function(err) {
			    }
			});

		}


	 	$("#new").on("click",function(e) {
 		 	e.preventDefault();

	 		$("#roleNm").focus();

 		 	if($("#upRoleSeq").val() ==''){
		 		alert("상위권한을 선택하세요");
 			 	return;
 		 	}

 		 	if($("#roleTypeCd").val() =='030'){
	 			 alert("상위권한을 선택하세요");
	 			 return;
 		 	}


	 		$("#save").show();
	 		$("#delete").hide();
	 		$("#roleMenuView").hide();

	 		$("#upRoleNm").val($("#roleNm").val());
	    	$("#upRoleSeq").val($("#roleSeq").val());


    	 	if($("#upRoleSeq").val() =='0'){
 				$("#new").hide();
 		 	}


	    	if($("#upRoleSeq").val() =='0'){
	    		//$("#roleTypeCd").val("010").attr("selected", "selected");
	    		$("#roleTypeCd").val("010").select2();
	 		 }else{
	 			 if($("#roleTypeCd").val()=='010'){
	 				//$("#roleTypeCd").val("020").attr("selected", "selected");
	 				$("#roleTypeCd").val("020").select2();
	 			 }

	 		 }


	    	$("#mode").val("new");

	    	$("#allMenu").hide();
	    	$("#saveRoleMenu").hide();

	    	$("#roleForm").show();



	    	 var selected = $('#roleTypeCd').val();

	    	 if(selected =='010' || selected =='020'){
	    		$( "#roleCd" ).val("");
	 			$( "#roleCd" ).prop( "readonly", true );
	 		 }
	    	 if(selected =='030'){
				 $( "#roleCd" ).prop( "readonly", false );
	 		 }


   	 		init();

       });


	 	$("#save").on("click",function(e) {


 		 	e.preventDefault();

 		 	 if($("#upRoleSeq").val() ==''){
	 			 alert("상위권한을 선택하세요");
	 			 return;
	 		 }


	 		if(!confirm("저장하시겠습니까?")) return;

	 		var params = $("#aform").serialize();


	 		$.ajax({
			    url:'/admin/auth/saveAuth',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){

			    		$("#roleTree").jstree("refresh_node", $("#upRoleSeq").val());
			  		    $("#roleTree").jstree("open_node", $("#upRoleSeq").val());

			    	}else{
			    		alert("저장에 실패했습니다.");
			    	}
			    },
			    error: function(err) {
			    }
			});

      });



		 $("#delete").on("click",function(e) {


		 		 e.preventDefault();


		 		if($("#roleSeq").val()==''){
		 			alert("삭제할 항목을 선택하세요.");
		 			return;

		 		}

		 		if($("#upRoleSeq").val()==-1){
		 			alert("최상위 항목을 삭제할수 없습니다.");
		 			return;
		 		}


		 		if(!confirm("삭제하시겠습니까?")) return;


		 		var params = $("#aform").serialize();

		 		$.ajax({
				    url:'/admin/auth/deleteAuth',
				    type:'post',
				    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
				    data: params,
				    success: function(data) {
				    	if(data.isSuccess){

				    		$("#roleTree").jstree("refresh_node", $("#upRoleSeq").val());
				  		    $("#roleTree").jstree("open_node", $("#upRoleSeq").val());


				    	}else{
				    		alert(data.msg);
				    		return;
				    	}
				    },
				    error: function(err) {
				    }
				});

	     });

	 	$("#saveRoleMenu").on("click",function(e) {

		 	e.preventDefault();
    	 	$("#roleMenuView").hide();
    	 	saveRoleMenu();

      });



	 	$("#roleTypeCd").change(function() {

	 		 var selected = $('#roleTypeCd').val();

	 		 if(selected =='010' || selected =='020'){
	 			$( "#roleCd" ).val("");
	 			$( "#roleCd" ).prop( "readonly", true );
	 		 }
			 if(selected =='030'){
				 $( "#roleCd" ).prop( "readonly", false );
	 		 }

	 	});
		//메뉴권한지정
		$("#roleMenuView").on("click",function(e) {


 		 	e.preventDefault();


 		 	$("#allMenu").show();
 			$("#roleForm").hide();
	 		$("#save").hide();
	 		$("#delete").hide();
	 		$("#saveRoleMenu").show();
 		 	$("#roleMenuView").hide();

     	});


	});






	function createRoleTree() {


		var schUseYn = $("#schUseYn").val();

		$('#roleTree').jstree("destroy");

        $('#roleTree').jstree({
        	 'core': {

        	      'data': {
	        	        "url" :  "/admin/auth/getAuthList",

	       	        	 "data": function (node) {
	       	        	 		 return {"useYn" :schUseYn, "roleSeq" : node.id};
	                      }
        	      } ,
        	      "check_callback" : true,
        	    },

        	    "types" : {

    	    	    "030" : {
    	    	      "icon" : "/images/framework/icon_opi.gif",
    	    	      "valid_children" : []
    	    	    }

    	    	  },


        	    "plugins" : [
        	    	"types"
        	    	//   "wholerow"
        	      ]

        });

        $('#roleTree').on("loaded.jstree", function (e, data) {
        	 $("#roleTree").jstree("open_all");
        });


        $('#roleTree').on("changed.jstree", function (e, data) {
        });

        $('#roleTree').on('open_node.jstree',function (e, data) {
        	showChildren(data);



        });


        $('#roleTree').on("select_node.jstree", function (e, data) {
        	showChildren(data);
        	createRoleMenuTree();
        });




   	 }




	function createRoleMenuTree() {

		$('#menuTree').jstree("destroy");

		 $.ajax({
             async: true,
             type: "POST",

             url: "/admin/menu/getAuthMenuList",
             data : {  "useYn" : "Y", "roleSeq" : $("#roleSeq").val() },
             dataType: "json",
             success: function (jsondata) {

            	 //console.log(JSON.stringify(jsondata));
            	    $('#menuTree').jstree({

            	    	 'core': {
                             'data': jsondata
                         },

            	    	'checkbox' : {
	           	        	keep_selected_style : false,
	            	        three_state : true, // to avoid that fact that checking a node also check others
	            	        // whole_node : false,  // to avoid checking the box just clicking the node
	            	         //tie_selection : false // for checking without selecting and selecting without checking
	            	      },

	            	      "types" : {

            	    	  	"last" : {
	               	    	      "icon" : "/images/filemanager/icon_doc.gif",
	               	    	      "valid_children" : []
               	    	    }

	          	    	  },

	            	      "plugins" : [
	              	    	 "checkbox", "types" ,"crrm"
	              	      ]

            	    });
             },

             error: function (xhr, ajaxOptions, thrownError) {
            	 if (xhr.status == 401 || xhr.status == 403) {
	                	alert("로그인을 하셔야 합니다.");
	                    location.replace('/login/loginPage');
	             } else {
	                 alert("예외가 발생했습니다. 관리자에게 문의하세요.");
	             }
             }
         });


        $('#menuTree').on('ready.jstree', function (e, data) {
           $("#menuTree").jstree("close_all");
           $("#menuTree").jstree("open_node", 0);
        })


        $('#menuTree').on("changed.jstree", function (e, data) {


        });

        $('#menuTree').on("select_node.jstree", function (e, data) {

        });


   	 }


	function saveRoleMenu(){

		if(!confirm("저장하시겠습니까?")) return;

		var roleCd=$("#roleCd").val();

		if(roleCd ==''){
			alert("권한을 선택하세요.");
			return;
		}

		var sendData ={};
		var roleSeq=$("#roleSeq").val();
		if(roleSeq == "") roleSeq=-1

		sendData.roleSeq = roleSeq;
		sendData.menu_seqs= $('#menuTree').jstree(true).get_selected();

		$.ajax({
		    url:'/admin/auth/saveAuthMenu',
		    type:'post',
		    contentType:"application/json; charset=UTF-8",

		    data: JSON.stringify(sendData),

		    success: function(data) {
		    	if(data.isSuccess){
		    		 $("#menuTree").jstree("open_all");
		    		 alert("저장되었습니다.");
		    	}else{
		    		alert("저장에 실패했습니다.");
		    	}
		    },
		    error: function(err) {
		    	alert("저장에 실패했습니다.");
		    }

		});

	}



	function showChildren(data) {

		$("#new").show();
		$("#save").show();
		$("#delete").show();
		$("#mode").val("update");

		$("#roleNm").val(data.node.text);

		if(data.node.original.roleTypeCd=='030'){
			$("#roleCd").val(data.node.original.roleCd);
		}else{
			 $('#saveRoleMenu').hide();
		}

    	$("#upRoleNm").val(data.node.original.upRoleNm);
    	$("#upRoleSeq").val(data.node.original.upRoleSeq);
    	$("#roleSeq").val(data.node.original.id);

    	//$("#useYn").val(data.node.original.useYn).attr("selected", "selected");
    	$("#useYn").val(data.node.original.useYn).select2();

    	//$("#roleTypeCd").val(data.node.original.roleTypeCd).attr("selected", "selected");

    	$("#roleTypeCd").val(data.node.original.roleTypeCd).select2();

    	$("#etc").val(data.node.original.etc); // 비고

	   	 if(data.node.original.id =='0'){
	   		 $("#delete").hide();
	   		 $("#save").hide();
	   		 $("#roleMenuView").hide();
	   	 }else{

	   		 if($('#saveRoleMenu').is(':visible') ){

	 			 	$("#save").hide();
	 	   			$("#delete").hide();
	 	   		 	$("#roleMenuView").hide();

	   		 }else{
	  			 	$("#save").show();
	  	   			$("#delete").show();
	  	   		 	$("#roleMenuView").show();
	   		 }

	   		if($('#allMenu').is(':visible') ){
	   			$("#save").hide();
	   			$("#delete").hide();
	   		}else{
	   			$("#save").show();
	   			$("#delete").show();
	   		}

	   	 }

  	 	var selected = $('#roleTypeCd').val();

    	 if(selected =='010' || selected =='020'){
    		$( "#roleCd" ).val("");
 			$( "#roleCd" ).prop( "readonly", true );
 		 }
    	 if(selected =='030'){
			 $( "#roleCd" ).prop( "readonly", false );
 		 }

     	// 정렬
     	var node = $("#roleTree").jstree("get_node",data.node.id);

 		var selected = $('#sortOrd option:selected');

     	$('#sortOrd').children('option').remove();

     	for (var i = 0; i < node.children.length; i++) {
     		var temp_node = $("#roleTree").jstree("get_node",node.children[i]);

          	 $('#sortOrd').append($('<option/>', {
                  value: temp_node.original.id,
                  text : temp_node.original.text
              }));
         }

     	$("#sortOrd").val(selected.val());

	}


	function init() {

		$("#roleNm").val('');
    	$("#roleCd").val('');
    	$("#roleSeq").val('');

    	//$("#useYn option:eq(0)").attr("selected", "selected");
	}


	//]]>
</script>

<section id="section" class="section">
	<div class="main-cont-box">
		<form name="aform" id="aform">
			<input type="hidden" name="upRoleSeq" id="upRoleSeq">
			<input type="hidden" name="roleSeq" id="roleSeq" value="-1">
			<input type="hidden" name="mode" id="mode">
			<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true" />
			<h2 class="page-title">
				<span>권한관리</span>
			</h2>
			<blockquote class="page-desc">사용자의 권한사용 여부를 관리할 수 있습니다.</blockquote>
			<div class="">
				<div class="popup-conts table-scroll">
					<div class="flex pc" style="height:900px;">
						<div class="flex-box1 column" style="height:550px;">
							<div class="inputbox">
								<div class="selectbox full mB10">
									<table>
										<tr>
											<th>권한사용여부</th>
											<td style="padding-right: 170px;">
												<div class="selectbox">
													<select name="schUseYn" id="selectBox" class="select-s" onChange="javascript:createRoleTree();">
														<option value="">전체</option>
														<option value="Y" selected>Yes</option>
														<option value="N">No</option>
													</select>
												</div>
											</td>
										</tr>
									</table>
								</div>
							</div>
							<div class="scroll1" style="height:550px;">
								<div class="tree-box" id="roleTree"></div>
							</div>
						</div>
						<div class="flex-box1" id="roleForm">
							<div class="scroll1 mL20" style="margin-top: 35px; height:520px;">
								<table class="table border2" style="height: 208px;">
									<colgroup>
										<col style="width: 30%">
										<col>
									</colgroup>

									<tr>
										<th><label for="upRoleNm">상위권한</label></th>
										<td colspan="3">
											<div class="inputbox">
												<input type="text" class="input-s" id="upRoleNm" name="upRoleNm" value="" readonly maxlength="100" />
											</div>
										</td>
									</tr>
									<tr>
										<th><label for="roleTypeCd">권한구분<span class="orange">*</span></label></th>
										<td colspan="3">
											<div class="selectbox" style="width: 100%;">
												<select name="roleTypeCd" id="roleTypeCd" class="select">
													<option value="010">시스템</option>
													<option value="020">폴더</option>
													<option value="030">권한</option>
												</select>
											</div>
										</td>
									</tr>
									<tr>
										<th><label for="roleNm">권한명<span class="orange">*</span></label></th>
										<td colspan="3">
											<div class="inputbox">
												<input type="text" class="input-s" id="roleNm" name="roleNm" value="" maxlength="100" />
											</div>
										</td>
									</tr>
									<tr>
										<th><label for="roleCd">권한코드</label></th>
										<td colspan="3">
											<div class="inputbox">
												<input type="text" class="input-s" id="roleCd" name="roleCd" value="" maxlength="100" />
											</div>
										</td>
									</tr>

									<tr>
										<th><label for="useYn">사용여부<span class="orange">*</span></label></th>
										<td colspan="3">
											<div class="selectbox" style="width: 100%;">
												<select id="useYn" name="useYn" class="select">
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
										<th><label for="etc">비고</label></th>
										<td colspan="3" rowspan="3">
											<textarea name="etc" id="etc" class="textarea"></textarea>
										</td>
									</tr>
								</table>
							</div>
						</div>

						<div class="flex-box1" id="allMenu" style="display: none;">
							<div class="scroll1 mL20" style="margin-top: 35px;">
								<div class="role-box" id="menuTree"></div>
							</div>
						</div>
					</div>
					<div class="list-back2">
						<ul class="btn-list">
							<li id="new"><a href="#" class="btn-submit-s navy">신규</a></li>
							<li id="save"><a href="#" class="btn-submit-s white">권한저장</a></li>
							<li id="roleMenuView"><a href="#" class="btn-submit-s gray btn-close">메뉴권한지정</a></li>
							<li id="saveRoleMenu"><a href="#" class="btn-submit-s gray btn-close">권한별 메뉴저장</a></li>
							<li id="delete"><a href="#" class="btn-submit-s red btn-close">삭제</a></li>
						</ul>
					</div>

				</div>
			</div>
		</form>
	</div>
	<!-- //end) .main-cont-box -->
</section>
