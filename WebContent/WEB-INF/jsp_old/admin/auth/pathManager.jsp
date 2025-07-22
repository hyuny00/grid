<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<script>//<![CDATA[




	$( document ).ready(function() {

		createPathTree();



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

	 	 	$("#pathTree").jstree("refresh_node", $("#pathSeq").val());
  		    $("#pathTree").jstree("open_node", $("#pathSeq").val());
       });


	 	$("#new").on("click",function(e) {
	 		 e.preventDefault();

	 		$("#path").focus();

	 		if($("#upPathSeq").val() !='-1'){
	 			 alert("상위경로를 선택하세요");
	 			 return;
	 		 }


	 		$("#save").show();
	 		$("#delete").hide();
	 		$("#pathRoleView").hide();

	 		$("#upPath").val($("#path").val());

	    	$("#upPathSeq").val($("#pathSeq").val());


	    	$("#mode").val("new");

	    	$("#allRole").hide();
	    	$("#savePathRole").hide();


	    	$("#pathForm").show();

	    	 init();

       });


	 	$("#save").on("click",function(e) {


	 		 e.preventDefault();

	 		if(!confirm("저장하시겠습니까?")) return;

	 		var params = $("#aform").serialize();

	 		$.ajax({
			    url:'/admin/auth/savePath',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){

			    		$("#pathTree").jstree("refresh_node", $("#upPathSeq").val());
			  		    $("#pathTree").jstree("open_node", $("#upPathSeq").val());


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



	 		if($("#pathSeq").val()==''){
	 			alert("삭제할 항목을 선택하세요.");
	 			return;

	 		}

	 		if($("#upPathSeq").val()==-1){
	 			alert("최상위 항목을 삭제할수 없습니다.");
	 			return;
	 		}


	 		if(!confirm("삭제하시겠습니까?")) return;

	 		var params = $("#aform").serialize();

	 		$.ajax({
			    url:'/admin/auth/deletePath',
			    type:'post',
			    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			    data: params,
			    success: function(data) {
			    	if(data.isSuccess){

			    		$("#pathTree").jstree("refresh_node", $("#upPathSeq").val());
			  		    $("#pathTree").jstree("open_node", $("#upPathSeq").val());


			    	}else{
			    		alert(data.msg);
			    		return;
			    	}
			    },
			    error: function(err) {
			    }
			});

     });




	 	$("#savePathRole").on("click",function(e) {


		 	e.preventDefault();

    	 	$("#pathRoleView").hide();

    	 	savePathRole();




      });



		$("#pathRoleView").on("click",function(e) {


 		 	e.preventDefault();


 		 	$("#allRole").show();
 			$("#pathForm").hide();
	 		$("#save").hide();
	 		$("#delete").hide();
	 		$("#savePathRole").show();
 		 	$("#pathRoleView").hide();

     	});


	});




	function updateOrd(){


		var pathOrd = [];
		$('#sortOrd option').each(function(){
			pathOrd.push($(this).val());
		});

		$.ajax({
		    url:'/admin/auth/updatePathOrd',
		    type:'post',
		    contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		    data:{path_seqs : pathOrd},
		    success: function(data) {
		    	/*
		    	if(data.isSuccess){
	    		  	$("#pathTree").jstree("refresh_node", $("#pathSeq").val());
		  		    $("#pathTree").jstree("open_node", $("#pathSeq").val());
		    	}
		    	*/
		    },
		    error: function(err) {
		    }
		});

	}




	function createPathTree() {
        $('#pathTree').jstree({
        	 'core': {

        	      'data': {
	        	        "url" :  "/admin/auth/getPathList/?lazy",

	       	        	 "data": function (node) {
	       	        	 		 return { "pathSeq" : node.id};
	                      }
        	      } ,
        	      "check_callback" : true,
        	    },

        	    "types" : {

    	    	    "030" : {
    	    	      "icon" : "/images/framework/icon_doc.gif",
    	    	      "valid_children" : []
    	    	    }

    	    	  },


        	    "plugins" : [
        	    	"types"
        	    	//   "wholerow"
        	      ]

        });


        $('#pathTree').on("changed.jstree", function (e, data) {

        });

        $('#pathTree').on('open_node.jstree',function (e, data) {
        	showChildren(data);
        });


        $('#pathTree').on("select_node.jstree", function (e, data) {
        	showChildren(data);

        	createPathRoleTree();
        });


   	 }




	function showChildren(data) {


		$("#save").show();
		$("#delete").show();
		$("#mode").val("update");

		$("#path").val(data.node.text);

		$("#pathNm").val(data.node.original.pathNm);



    	$("#upPathSeq").val(data.node.original.upPathSeq);
    	$("#pathSeq").val(data.node.original.id);
    	$("#upPath").val(data.node.original.upPath);




   		 if(data.node.original.id =='0'){
    		 $("#delete").hide();
    		 $("#save").hide();
    		 $("#pathRoleView").hide();
    	 }else{
    		// $("#delete").show();
    		// $("#save").show();

       		 if($('#savePathRole').is(':visible') ){
       			$("#save").hide();
       			$("#delete").hide();
       		 	$("#pathRoleView").hide();
       		 }else{
       			 $("#save").show();
       			$("#delete").show();
       		 	$("#pathRoleView").show();
       		 }
    	 }




   		var node = $("#pathTree").jstree("get_node",data.node.id);

   		var selected = $('#sortOrd option:selected');
       	$('#sortOrd').children('option').remove();
       	for (var i = 0; i < node.children.length; i++) {
       		var temp_node = $("#pathTree").jstree("get_node",node.children[i]);
            	 $('#sortOrd').append($('<option/>', {
                    value: temp_node.original.id,
                    text : temp_node.original.text
                }));

        }
       	$("#sortOrd").val(selected.val());


	}


	function init() {
		$("#path").val('');
		$("#pathNm").val('');
    	$("#pathSeq").val('');
	}



	function createPathRoleTree() {

		$('#roleTree').jstree("destroy");

		var pathSeq=$("#pathSeq").val();
 		if(pathSeq == "") pathSeq=-1

 		 $.ajax({
             async: true,
             type: "POST",
             url: "/admin/auth/getPathAuthList",
             data : {  "useYn" : "Y",  "pathSeq" : pathSeq },
             dataType: "json",
             success: function (jsondata) {
            	    $('#roleTree').jstree({

            	    	 'core': {
                             'data': jsondata
                         },

            	    	'checkbox' : {
	           	        	keep_selected_style : true,
	            	        three_state : true, // to avoid that fact that checking a node also check others
	            	         //whole_node : false,  // to avoid checking the box just clicking the node
	            	         //tie_selection : false // for checking without selecting and selecting without checking
	            	      },

	            	      "types" : {

	            	    	  "030" : {
	              	    	      "icon" : "/images/framework/icon_opi.gif",
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




        $('#roleTree').on('ready.jstree', function (e, data) {
        	 $("#roleTree").jstree("open_all");
        })


        $('#roleTree').on("changed.jstree", function (e, data) {

        });

        $('#roleTree').on("select_node.jstree", function (e, data) {

        });



   	 }

	function savePathRole(){

		if(!confirm("저장하시겠습니까?")) return;



		var sendData ={};
		var pathSeq=$("#pathSeq").val();
		if(pathSeq == "") pathSeq=-1

		sendData.pathSeq = pathSeq;
		sendData.role_seqs= $('#roleTree').jstree(true).get_selected();

		$.ajax({
		    url:'/admin/auth/savePathAuth',
		    type:'post',
		    contentType:"application/json; charset=UTF-8",

		    data: JSON.stringify(sendData),

		    success: function(data) {
		    	if(data.isSuccess){
		    		 $("#roleTree").jstree("open_all");
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


	//]]>
</script>
<section id="section" class="section">
	<div class="main-cont-box">
		<form name="aform" id="aform">
			<input type="hidden" name="upPathSeq" id="upPathSeq">
			<input type="hidden" name="pathSeq" id="pathSeq" value="-1">
			<input type="hidden" name="mode" id="mode">
			<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true" />
			<h2 class="page-title">
				<span>리소스관리</span>
			</h2>
			<blockquote class="page-desc">url주소의 권한을 관리하실수 있습니다</blockquote>
			<div class="">
				<div class="popup-conts table-scroll">
					<div class="flex pc">
						<div class="flex-box column">
							<div class="scroll2">
								<div class="tree-box" id="pathTree"></div>
							</div>
						</div>
						<div class="flex-box" id="pathForm">
							<div class="mL20">
								<div class="Manager">
									<table class="table border2">
										<colgroup>
											<col style="width: 30%">
											<col>
										</colgroup>
										<tr>
											<th><label for="upPath">상위경로</label></th>
											<td>
												<div class="inputbox">
													<input type="text" class="input" id="upPath" name="upPath" value="" maxlength="100" />
												</div>
											</td>
										</tr>
										<tr>
											<th><label for="path">경로<span class="orange">*</span></label></th>
											<td>
												<input type="text" class="input" id="path" name="path" value="" maxlength="100" />
											</td>
										</tr>
										<tr>
											<th><label for="pathNm">경로명<span class="orange">*</span></label></th>
											<td>
												<input type="text" class="input" id="pathNm" name="pathNm" value="" maxlength="100" />
											</td>
										</tr>
										<tr>
											<th><label for="sortOrd">정렬순서</label></th>
											<td>
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
													<ul id="sortList" class="textarea" style="height: 280px;">
														<li style="width: 100%; height: 100%;"><select id="sortOrd" name="sortOrd" multiple style="width: 100%; height: 100%;">
															</select></li>
													</ul>
												</div>
											</td>
										</tr>
									</table>
								</div>
							</div>
						</div>

						<div class="flex-box" id="allRole" style="display: none;">
							<div class="scroll2 mL20">
								<div id="roleTree" class="auth-box"></div>
							</div>
						</div>
					</div>
					<div class="list-back1">
						<ul class="btn-list">
							<li id="new"><a href="#" class="btn-submit-s navy">신규</a></li>
							<li id="save" style="display: none"><a href="#" class="btn-submit-s white">저장</a></li>
							<li id="pathRoleView"><a href="#" class="btn-submit-s gray btn-close">경로별 권한지정</a></li>
							<li id="savePathRole" style="display: none"><a href="#" class="btn-submit-s white">경로별 권한저장</a></li>
							<li id="delete" style="display: none"><a href="#" class="btn-submit-s red">삭제</a></li>
						</ul>
					</div>

				</div>
			</div>
		</form>
	</div>
	<!-- //end) .main-cont-box -->
</section>
