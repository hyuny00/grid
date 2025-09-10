

var uploadModule =(function() {

	 var basePath;

	 var random;

	 $(document).ready(function(){
		 random=Math.random();
		 $('[data-file-list]').each(function(){

			 basePath = $(this).data("basePath");

			 var noButton = $(this).data("noButton");


			 getFileList(this);
			 //파일드롭다운기능 초기화
			 if($(this).data("mode") !='view' && noButton!='Y'){
				 fileDropDown(this);
			 }


		 });


		$('body').append('<form name="downloadForm" id="downloadForm" method="post" action="'+basePath+'/file/download/zip" style="display:none"><input type="hidden" name="downloadFileInfo" id="downloadFileInfo" value=""><input type="hidden" name="_csrf" value="'+token+'"></form>');

	 });


	 function reload(){

		 random=Math.random();

		 $('[data-file-list]').each(function(){

			 basePath = $(this).data("basePath");

			 getFileList(this);
			 //파일드롭다운기능 초기화
			 if($(this).data("mode") !='view'){
				 fileDropDown(this);
			 }
		 });

	 }


	 function setFileDetail(docId,fileId, fileNm){

		 if($("#clickFileId").length){
			 $("#clickFileId").val(fileId);
		 }
		 if($("#clickFileNm").length){
			 $("#clickFileNm").val(fileNm);
		 }
	 }


	 function getFileList(obj){

		 var docId = $(obj).data("docId");
		 var uploadFormId = $(obj).data("uploadFormId");


		 if(docId !== ''){
			  $.ajax({

				    type: "GET",
				    url: basePath+"/file/fileList",
				    data: {docId:docId},
				    datatype : "json",
				    contentType: "application/json",
				    cache: false,

				    success: function (result) {
				    	var fileInfos = result.fileInfo;
				    	fileInfos.forEach(function(fileInfo) {
				    		 fileInfo.delYn='N';
				    		 displayFileList(fileInfo, uploadFormId);
				    	});

				    },
		            error : function(jqXHR) {
		                if (jqXHR.status == 401) {
		                	alert("로그인을 하셔야 합니다.");
		                    location.replace('/login/loginPage');
		                } else if (jqXHR.status == 403) {
		                	 $("#progressLabel_"+uploadFormId).html("파일업로드 권한이 없습니다.");
		                	 $("#file_input_"+uploadFormId).attr('disabled', true);
		                } else {
		                    alert("예외가 발생했습니다. 관리자에게 문의하세요.");
		                }
		            }

				});
		 }

	 }

	 var chunk_size = 1*1024*1024*5; // 5Mbyte Chunk, was의 chunkSize 보다 같거나 작게 잡아야 함

	 //파일선택업로드
	 function upload(uploadFormId) {



		 if( $("#fileInfoList_"+uploadFormId).val() != ''){

			 var fileObj = {};
			 var tmpFileList =[];

			 fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
			 tmpFileList = fileObj.fileInfo;


			 var upFile=0;
			 for( var i=0; i< tmpFileList.length; i++){
				 if(tmpFileList[i].delYn!='Y'){
					 upFile++;
				 }
			 }

			var maxFileCnt=  $("#fileList_"+uploadFormId).data("maxFileCnt");
			if(maxFileCnt!=''){
				if(upFile >= maxFileCnt){
					alert("파일업로드는 "+maxFileCnt+ "개 까지 할수 있습니다.");
					return;
				}
			}

		 }


		 var file_input = document.getElementById("file_input_"+uploadFormId);
		 var files = file_input.files;

		 var fileList = getFileArray(uploadFormId, files);

		 setUploadInfo(fileList, uploadFormId)
	 }

	 // 파일 드롭 다운 업로드
	// 파일 드롭 다운 업로드 - 개선된 버전
	// 파일 드롭 다운 업로드 - 개선된 버전
	 function fileDropDown(obj) {
	     var uploadFormId = $(obj).data("uploadFormId");
	     var dropZone = $(obj); // 전체 file-upload 영역을 드롭존으로 설정
	     var mode = $(obj).data("mode"); // 모드 확인

	     // view 모드일 때는 드래그 스타일 적용하지 않음
	     if (mode === 'view') {
	         return;
	     }

	     // 드래그 오버 시 시각적 피드백을 위한 스타일 추가
	     var originalStyle = {
	         backgroundColor: dropZone.css('background-color'),
	         border: dropZone.css('border'),
	         opacity: dropZone.css('opacity')
	     };

	     // Drag Enter - 드래그가 영역에 들어올 때
	     dropZone.on('dragenter', function(e) {
	         e.stopPropagation();
	         e.preventDefault();

	         // 드래그 오버 시각적 효과 적용
	         dropZone.addClass('dragover');
	         dropZone.css({
	             'background-color': '#e3f2fd',
	             'border': '2px dashed #2196f3',
	             'opacity': '0.8'
	         });

	         // 드롭 가능 메시지 표시
	         if (!dropZone.find('.drag-message').length) {
	             dropZone.append('<div class="drag-message" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(33, 150, 243, 0.9); color: white; padding: 10px 20px; border-radius: 5px; font-size: 14px; z-index: 1000;">파일을 여기에 드롭하세요</div>');
	         }
	     });

	     // Drag Leave - 드래그가 영역을 벗어날 때
	     dropZone.on('dragleave', function(e) {
	         e.stopPropagation();
	         e.preventDefault();

	         // 실제로 영역을 벗어났는지 확인 (자식 요소로 이동하는 경우 제외)
	         var rect = this.getBoundingClientRect();
	         var x = e.originalEvent.clientX;
	         var y = e.originalEvent.clientY;

	         if (x <= rect.left || x >= rect.right || y <= rect.top || y >= rect.bottom) {
	             // 원래 스타일로 복구
	             dropZone.removeClass('dragover');
	             dropZone.css({
	                 'background-color': originalStyle.backgroundColor,
	                 'border': originalStyle.border,
	                 'opacity': originalStyle.opacity
	             });

	             // 드롭 메시지 제거
	             dropZone.find('.drag-message').remove();
	         }
	     });

	     // Drag Over - 드래그 중일 때 (필수: drop 이벤트를 위해)
	     dropZone.on('dragover', function(e) {
	         e.stopPropagation();
	         e.preventDefault();

	         // 드롭 가능 효과 유지
	         dropZone.addClass('dragover');

	         // 드롭 효과 표시
	         e.originalEvent.dataTransfer.dropEffect = 'copy';
	     });

	     // Drop - 파일이 드롭될 때
	     dropZone.on('drop', function(e) {
	         e.preventDefault();
	         e.stopPropagation();

	         // 드래그 효과 제거
	         dropZone.removeClass('dragover');
	         dropZone.css({
	             'background-color': originalStyle.backgroundColor,
	             'border': originalStyle.border,
	             'opacity': originalStyle.opacity
	         });

	         // 드롭 메시지 제거
	         dropZone.find('.drag-message').remove();

	         // 업로드 중 확인
	         var disabled = $("#file_input_" + uploadFormId).attr('disabled');

	         if (isDefaultStr(disabled) == '') {
	             var files = e.originalEvent.dataTransfer.files;
	             if (files != null) {
	                 if (files.length < 1) {
	                     return;
	                 }

	                 // 드롭 성공 시 잠시 성공 메시지 표시
	                // dropZone.append('<div class="drop-success" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(76, 175, 80, 0.9); color: white; padding: 10px 20px; border-radius: 5px; font-size: 14px; z-index: 1000;">파일이 추가되었습니다!</div>');

	                 setTimeout(function() {
	                     dropZone.find('.drop-success').fadeOut(300, function() {
	                         $(this).remove();
	                     });
	                 }, 1500);

	                 var fileList = getFileArray(uploadFormId, files);
	                 setUploadInfo(fileList, uploadFormId);
	             } else {
	                 alert("ERROR");
	             }
	         } else {
	             alert("업로드 중에는 사용하실수 없습니다.");
	         }
	     });

	     // 전체 문서에서 드래그 이벤트 차단 (브라우저 기본 동작 방지)
	     $(document).on('dragover drop', function(e) {
	         e.preventDefault();
	     });
	 }

	//업로드 정보세팅
	 function setUploadInfo(fileList, uploadFormId){

		 var acceptFile =  $("#fileList_"+uploadFormId).data("acceptFile");

		 if(fileList){

			 var metadata= {};
			 metadata.uploadFormId = uploadFormId;
			 metadata.fileIndex =0;

			 for(var i=0; i<fileList.length; i++){
				 if(fileList[i].size == 0){
					 fileList.splice(i,1);
				 }
			 }

			 metadata.fileList= fileList;

			 var maxFileSize=  $("#fileList_"+uploadFormId).data("maxFileSize");
			 if( isNotEmpty(maxFileSize)){
				 metadata.maxFileSize = maxFileSize.substr(0, maxFileSize.length-1);
			 }else{
				 metadata.maxFileSize = 0;
			 }
			 if(acceptFile==='.zip'){
				 metadata.isOnlyZip='Y';
			 }

			 var thumbnailYn=  $("#fileList_"+uploadFormId).data("thumbnailYn");
			 if(thumbnailYn==='Y'){
				 metadata.thumbnailYn='Y';
			 }

			 processFile(metadata);
		 }
	 }


	 function processFile(metadata) {
		 if( metadata.fileList.length > metadata.fileIndex ){
			 setFileInfo(metadata);
		 }else{
			 $("#file_input_"+metadata.uploadFormId).val(null);
		 }
	 }


	 function setFileInfo(metadata) {


		 metadata.start = 0;
		 metadata.totalChunks = 0;
		 metadata.chunkIndex = 1;
		 metadata.fileId = "";
		 metadata.fileSize=0;

		 var file = metadata.fileList[metadata.fileIndex];

		 if(file) {

			 var fileSize = file.size;

			// if(fileSize > 1024*1024*1000){
				// chunk_size =1024*1024*30;
			// }

			 var totalChunks = Math.ceil(file.size / chunk_size);

			 metadata.end = chunk_size;


			 metadata.fileNm =file.name;
			 metadata.totalChunks = totalChunks;
			 metadata.fileSize=fileSize;

			 $("#progressLabel_"+metadata.uploadFormId).html("Starting...");
			 $("#file_input_"+metadata.uploadFormId).attr('disabled', true);

			 var startTime= new Date().getTime();
			 metadata.startTime=startTime;
			 slice(metadata);
		 }

	 }

	 function uploadFile(piece, metadata){

		 var formdata = new FormData();

		 //metadata.fileNm =  encodeURIComponent(metadata.fileNm);

		 formdata.append('file',  piece);
		 formdata.append('metadata', JSON.stringify(metadata));

		// console.log(JSON.stringify(metadata));
		  $.ajax({


			    type: "POST",
			    url: basePath+"/file/upload",
			    data: formdata,
			    contentType: false,
			    processData: false,
			    cache: false,

			    success: function (result) {

			    	if(result.hasOwnProperty("errorCode")){
			    		$("#progressLabel_"+metadata.uploadFormId).html(result.errorMessage );
			    		$("#file_input_"+metadata.uploadFormId).attr('disabled', false);
			    		return;
			    	}

			    	var fileInfo = result.fileInfo[0];

			    	metadata.chunkIndex++;


			    	if(fileInfo.uploadComplete != 'Y'){

			    		  metadata.fileId=fileInfo.fileId;
			    		  metadata.start += chunk_size;
			    		  metadata.end = metadata.start + chunk_size;

			    		  metadata.fileNm =fileInfo.fileNm;

				    	  if (metadata.fileSize - metadata.end < 0) {
				    		  metadata.end = metadata.fileSize;
				    	  }

				    	  var number=Math.round(100*metadata.start/metadata.fileSize);
				    	  $("#progressBar_"+metadata.uploadFormId).show();
				    	  $("#progressBar_"+metadata.uploadFormId).val(number);



				    	  slice(metadata);
			    	}else{

			    		 var startTime= metadata.startTime;
						 var endTime=new Date().getTime();
						 var uploadTime=  endTime-startTime;

						 var spd=(metadata.fileSize/(1024*1024)/ (uploadTime/1000)).toFixed(2);
						 if(spd==0.00){
							 spd='';
						 }else{
							 spd = "["+(metadata.fileSize/(1024*1024)/ (uploadTime/1000)).toFixed(2)+"M/sec]";
						 }

			    		$("#progressBar_"+metadata.uploadFormId).val(100);


			    		$("#progressLabel_"+metadata.uploadFormId).html("upload completed: " +"100% " +spd);
			    		$("#file_input_"+metadata.uploadFormId).attr('disabled', false);


			    		metadata.fileId=fileInfo.fileId;
			    		metadata.fileIndex++;

			    		metadata.fileNm =fileInfo.fileNm;

		    			processFile(metadata);

			    		displayFileList(fileInfo, metadata.uploadFormId);

			    		$("#progressBar_"+metadata.uploadFormId).hide();
			    		$("#progressBar_"+metadata.uploadFormId).val(0);
			    	}

			    },
			    error: function (result) {
                    $("#progressLabel_"+metadata.uploadFormId).html("Upload failed");
			    }
			});
	 }


	 function displayFileList(fileInfo, uploadFormId) {
		    var fileObj = {};
		    var tmpFileList = [];
		    if ($("#fileInfoList_" + uploadFormId).val() != '') {
		        fileObj = JSON.parse($("#fileInfoList_" + uploadFormId).val());
		        tmpFileList = fileObj.fileInfo;
		        if (fileInfo.fileNm != '') {
		            // 빈 메시지 숨기기
		            $("#emptyMessage_" + uploadFormId).hide();
		         // 파일 리스트 표시 (핵심 수정사항)
		            $("#fileList_" + uploadFormId).show();
		        }
		    } else {
		        $("#fileList_" + uploadFormId).html("");
		    }
		    if (fileInfo.delYn == 'N') {
		        var found = false;
		        tmpFileList.forEach(function(item, index) {
		            if (item.fileId == fileInfo.fileId) {
		                found = true;
		            }
		        });
		        if (!found) {

		        	// 첫 번째 파일 추가 시 리스트 표시
		        	$("#fileList_" + uploadFormId).show();

		            tmpFileList.push(fileInfo);
		            fileObj.refDocId = $("#refDocId_" + uploadFormId).val();
		            fileObj.docId = $("#docId_" + uploadFormId).val();
		            fileObj.fileInfo = tmpFileList;
		            var fileNm = fileInfo.fileNm;
		            var checked = "";
		            if (fileInfo.selected) {
		                checked = "checked";
		            }
		            var requiredAttachIndex = $("#fileList_" + uploadFormId).data("requiredAttachIndex");
		            if (requiredAttachIndex != '') {
		                var refDocId = $("#refDocId_" + uploadFormId).val();
		                $("#" + refDocId + "_" + requiredAttachIndex).val("Y");
		            }
		            var fileFunStr = 'uploadModule.setFileDetail("' + fileInfo.docId + '","' + fileInfo.fileId + '", "' + fileInfo.fileNm + '");';

		            // 파일 확장자에 따른 CSS 클래스 결정
		            var fileExtension = getFileExtension(fileNm);
		            var fileClass = getFileClass(fileExtension);

		            // 파일명에서 작은따옴표 이스케이프 처리
		            var safeFileNm = fileInfo.fileNm.replace(/'/g, "&#39;");
		            var safeUploadFormId = uploadFormId.replace(/'/g, "&#39;");

		            // li 태그로 감싸서 파일 목록 표시 - label 구조 유지하면서 다운로드 링크 분리

		            if($("#refDocId_"+uploadFormId).val()=="NONE"){
		            	 $("#fileList_" + uploadFormId).append(
					                "<li>" +

				                            "<a href='javascript:void(0);' onclick=\"event.stopPropagation(); uploadModule.fileDownload('" + safeUploadFormId + "', '" + fileInfo.fileId + "', '" + safeFileNm + "','" + fileInfo.temp + "');\" class='" + fileClass + "' style='text-decoration:none;'>" + fileNm +" ["+Math.round(Number(fileInfo.fileSize/1024))+"k]</a>" +

					                "</li>"
					            );

			          }else{
			            $("#fileList_" + uploadFormId).append(
			                "<li>" +
			                    "<div data-file-Id='" + fileInfo.fileId + "' style='padding-left:8px'>" +
			                        "<input onclick='" + fileFunStr + "' class='check' id='" + random + "-" + fileInfo.fileId + "' type='checkbox' " + checked + " name='file_" + uploadFormId + "' value='" + fileInfo.fileId + "'> " +
			                        "<label class='label' for='" + random + "-" + fileInfo.fileId + "'>" +
			                            "<a href='javascript:void(0);' onclick=\"event.stopPropagation(); uploadModule.fileDownload('" + safeUploadFormId + "', '" + fileInfo.fileId + "', '" + safeFileNm + "','" + fileInfo.temp + "');\" class='" + fileClass + "' style='text-decoration:none;'>" + fileNm +" ["+Math.round(Number(fileInfo.fileSize/1024))+"k]</a>" +
			                        "</label>" +
			                    "</div>" +
			                "</li>"
			            );
			          }
		        }
		    } else {
		        // 파일 삭제 처리
		        $("#fileList_" + uploadFormId + " > li").each(function() {
		            if ($(this).find("input[value='" + fileInfo.fileId + "']").length > 0 && fileInfo.delYn == 'Y') {
		                $(this).remove();
		            }
		        });
		        fileObj.fileInfo.forEach(function(obj, index) {
		            if (obj.fileId == fileInfo.fileId && fileInfo.delYn == 'Y') {
		                if (obj.temp == 'Y') {
		                    fileObj.fileInfo.splice(index, 1);
		                } else {
		                    obj.delYn = 'Y';
		                }
		            }
		        });
		        // 파일이 모두 삭제되면 빈 메시지 표시
		        if (fileObj.fileInfo.length == 0 || fileObj.fileInfo.every(f => f.delYn == 'Y')) {
		            $("#emptyMessage_" + uploadFormId).show();

		            $("#fileList_" + uploadFormId).hide();

		            var requiredAttachIndex = $("#fileList_" + uploadFormId).data("requiredAttachIndex");
		            if (requiredAttachIndex != '') {
		                var refDocId = $("#refDocId_" + uploadFormId).val();
		                $("#" + refDocId + "_" + requiredAttachIndex).val("");
		            }
		        }
		    }
		    $("#fileInfoList_" + uploadFormId).val(JSON.stringify(fileObj));
		}


	 function deleteAllFile(uploadFormId){

		 $("input[name=file_"+uploadFormId+"]").prop("checked",true);
		 uploadModule.deleteFile(uploadFormId);
	 }

	 function deleteFile(uploadFormId){

		 if(!confirm("삭제하시겠습니까?")){
			 $("input[name=file_"+uploadFormId+"]").prop("checked",false);
			 return;
		 }

		 var deleteFile = [];
		 var fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
		 $("input[name=file_"+uploadFormId+"]:checked").each(function(){
			 	var fileInfo ={};

			 	var tmpFileId=$(this).val();


				 fileObj.fileInfo.forEach(function(obj, index) {
					 if(obj.fileId == tmpFileId){

						 fileInfo.fileId = obj.fileId;
						 fileInfo.docId= obj.docId;
						 fileInfo.temp = obj.temp;
						 fileInfo.fileNm= obj.fileNm;

						 deleteFile.push(fileInfo);
					 }
				 });

		 });


		 if(deleteFile.length==0){
			 alert("삭제할 파일을 선택하세요");
			 return;
		 }


		  $.ajax({

			    type: "POST",
			    url: basePath+"/file/deleteFile",
			    data: JSON.stringify(deleteFile),
			    dataType: "json",
                contentType: "application/json; charset=utf-8",
			    cache: false,

			    success: function (result) {

			    	if(result.hasOwnProperty("errorCode")){
			    		$("#progressLabel_"+uploadFormId).html(result.errorMessage );
			    		return;
			    	}

			    	var fileMsg="";
			    	var fileInfos = result.fileInfo;
			    	fileInfos.forEach(function(fileInfo) {
			    		if(fileInfo.fileMsg=="FILE-08"){
			    			fileMsg+=fileInfo.fileNm+", "
			    		}else{
			    			displayFileList(fileInfo, uploadFormId);
			    		}
			    	});

			    	$("#checkAll_"+uploadFormId).prop("checked", false) ;

			    	if(fileMsg != ''){
				    	fileMsg = fileMsg.substr(0, fileMsg.lastIndexOf(","));
				    	fileMsg = "["+fileMsg+"] 는 삭제할 권한이 없습니다."
			    		$("#progressLabel_"+uploadFormId).html(fileMsg );
			    	}else{
			    		$("#progressLabel_"+uploadFormId).html("delete completed");
			    	}


			    },
			    error: function (result) {
                    $("#progressLabel_"+uploadFormId).html("delete failed");
			    }
			});

	 }


	 function slice(metadata) {


		 if(metadata.totalChunks >= metadata.chunkIndex){



			 var file = metadata.fileList[metadata.fileIndex];
			 var slice = file.mozSlice ? file.mozSlice :
		               file.webkitSlice ? file.webkitSlice :
		               file.slice ? file.slice : noop;

			 var piece = slice.bind(file)(metadata.start, metadata.end);

			 var startTime= metadata.startTime;
			 var endTime=new Date().getTime();
			 var uploadTime=  endTime-startTime;

			 var spd=(metadata.end/(1024*1024)/ (uploadTime/1000)).toFixed(2);
			 if(spd==0.00){
				 spd='';
			 }else{
				 spd = "["+(metadata.end/(1024*1024)/ (uploadTime/1000)).toFixed(2)+"M/sec]";
			 }


			 $("#progressLabel_"+metadata.uploadFormId).html("Uploading: [파일명 :" +  metadata.fileNm +"]"+ (100*metadata.start/file.size).toFixed(0) + "% "+spd );
			 uploadFile(piece, metadata);

		 }
	 }

	 function noop() {

	 }


	 function download(uploadFormId){

		 var fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
		 var downloadFile = [];
		 $("input[name=file_"+uploadFormId+"]:checked").each(function(){
			 	var fileInfo ={};

			 	var tmpFileId=$(this).val();
			 	 fileObj.fileInfo.forEach(function(obj, index) {
					 if(obj.fileId == tmpFileId){

						 fileInfo.fileId = obj.fileId;
						 fileInfo.fileNm= obj.fileNm;
						 fileInfo.filePath= obj.filePath;
						 fileInfo.fileAuth= obj.fileAuth;
						 downloadFile.push(fileInfo);
					 }
				 });

		 });


		 if(downloadFile.length==0){
			 alert("다운로드할 파일을 선택하세요");
			 return;
		 }



		 $('#downloadFileInfo').val(JSON.stringify(downloadFile));
		 $("#downloadForm").attr("action", basePath+"/file/download/zip");
		 $("#downloadForm").submit();

	 }


	 function allDownload(uploadFormId){

		 var fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
		 var downloadFile = [];


		var fileInfo ={};

	 	 fileObj.fileInfo.forEach(function(obj, index) {

	 		 /*
			 fileInfo.fileId = obj.fileId;
			 fileInfo.fileNm= obj.fileNm;
			 fileInfo.filePath= obj.filePath;

			 console.log("fileInfofileInfofileInfofileInfofileInfo",fileInfo);
*/
			 downloadFile.push(obj);
		 });




		 /*
		 $("input[name=file_"+uploadFormId+"]").each(function(){

			 	$(this).prop("checked",true);

			 	var fileInfo ={};

			 	var tmpFileId=$(this).val();
			 	 fileObj.fileInfo.forEach(function(obj, index) {
					 if(obj.fileId == tmpFileId){

						 fileInfo.fileId = obj.fileId;
						 fileInfo.fileNm= obj.fileNm;
						 fileInfo.filePath= obj.filePath;

						 downloadFile.push(fileInfo);
					 }
				 });

		 });


		 if(downloadFile.length==0){
			 alert("다운로드할 파일을 선택하세요");
			 return;
		 }
*/

		 $('#downloadFileInfo').val(JSON.stringify(downloadFile));
		 $("#downloadForm").attr("action", basePath+"/file/download/zip");

		 $("#downloadForm").submit();

	 }

	 function fileDownload(uploadFormId, fileId, fileNm, temp){

		var fileInfo ={};
		fileInfo.fileId=fileId;
		fileInfo.fileNm=fileNm;

		fileInfo.temp=temp;
		if(temp !='Y')
		fileInfo.temp='N';

		$.ajax({
			url		: "/file/isExistFile",
			type	: "post",
			data	: fileInfo,
			dataType : "json",
			success : function(data, textStatus) {

				if(data.msg=="SUCCESS"){
					$('#downloadFileInfo').val(JSON.stringify(fileInfo));
					$("#downloadForm").attr("action", basePath+"/file/download");


					$("#downloadForm").submit();
				}else{
					alert(data.msg);
				}
			},
			error: function(){
				alert("FAIL");
			}
		});

	 }




    //파일 중복금지
    function getFileArray(uploadFormId, files){

    	 $("#progressLabel_"+uploadFormId).html('');

    	 var fileObj = {};
    	 var fileList = [];
    	 var tmpFileList =[];

    	 var acceptFile=  $("#fileList_"+uploadFormId).data("acceptFile");
    	 var singleFile=  $("#fileList_"+uploadFormId).data("singleFile");

    	 var maxFileSize=  $("#fileList_"+uploadFormId).data("maxFileSize");


    	 var maxFileCnt=  $("#fileList_"+uploadFormId).data("maxFileCnt");


		 if( $("#fileInfoList_"+uploadFormId).val() != ''){
			 fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
		 }

		 tmpFileList =getFileInfo( fileObj.fileInfo);

    	 var upFile=0;

		 for( var i=0; i< tmpFileList.length; i++){
			 if(tmpFileList[i].delYn!='Y'){
				 upFile++;
			 }

		 }

    	 if(singleFile == 'Y'){
			 if(files.length+upFile  >1){
				 $("#progressLabel_"+uploadFormId).html("한개의 파일만 업로드 가능합니다.");
				 return;
			 }
		 }

    	 if(maxFileCnt != ''){
			 if(files.length+upFile  > maxFileCnt){
				 $("#progressLabel_"+uploadFormId).html(maxFileCnt+"개 파일만 업로드 가능합니다.");
				 return;
			 }
		 }



		 tempFileList = [];

		 if(fileObj.hasOwnProperty("fileInfo")){


			 if(singleFile == 'Y'){
				 var cnt=0;
				 fileObj.fileInfo.forEach(function(obj, index) {
					 if(obj.delYn!= 'Y'){
						 cnt++;
					 }
				 });
				 if(cnt+files.length  >1){
					 $("#progressLabel_"+uploadFormId).html("한개의 파일만 업로드 가능합니다.");
					 return;
				 }

			 }

			 fileObj.fileInfo.forEach(function(obj, index) {
				 if(obj.temp == 'Y'){

				    var fileNm= obj.fileNm;

				    tempFileList.push(fileNm);
				 }
			 });
		 }


		 var fileNm;
		 var fileExt;
		 for(var i=0; i<files.length; i++){

			 fileNm=files[i].name

			 if (fileNm.lastIndexOf(".") > 0) {
				 fileExt = fileNm.substring(fileNm.lastIndexOf("."), fileNm.length);
		     }

			 if(acceptFile.toUpperCase().indexOf(fileExt.toUpperCase()) == -1){
				 $("#progressLabel_"+uploadFormId).append("<p>["+fileNm+"] : 파일확장자가 "+fileExt+ "인 파일은 업로드 할수 없습니다.</p>");
				 continue;
			 }

			 var tmpmaxFileSize;
			 if( isNotEmpty(maxFileSize)){
				 tmpmaxFileSize = maxFileSize.substr(0, maxFileSize.length-1);
				 if(files[i].size > tmpmaxFileSize*1024*1024 ){
					 $("#progressLabel_"+uploadFormId).append("<p>["+fileNm+"] : 파일사이즈가 "+maxFileSize+ "이하만 업로드 가능합니다.</p>");
					 continue;
				 }
			 }else{
				 tmpmaxFileSize = 0;
			 }


			 if( tempFileList.indexOf(files[i].name) != -1){
				 $("#progressLabel_"+uploadFormId).html('');
				 $("#progressLabel_"+uploadFormId).append("<p>["+fileNm+"] : 파일명이 같은 파일이 존재합니다.</p>");
				 continue;
			 }
			 fileList.push(files[i]);
		 }

		 return fileList;
    }


    function fnFileImg(fileNm){

	     var fileGif=new Array('bmp','doc','docx','etc','exe', 'gif', 'gul','htm','html','hwp', 'ini','jpg', 'mgr', 'mpg', 'pdf', 'ppt', 'pptx','print', 'tif', 'txt', 'wav', 'xls', 'xlsx', 'xml', 'zip');
	     if(fileNm == ''){
	      return '';
	     }

	     var start = fileNm.lastIndexOf(".");
	     var name = '';
	     if( start  > -1){
	      name = fileNm.substring(start+1).toLowerCase();
	     }

	     var retFlag = false;
	     for(fileInx =0; fileInx< fileGif.length;fileInx++){
	      if(name == fileGif[fileInx]){
	       retFlag = true;
	       break;
	      }
	     }
	     var retStr = '';
	     if(retFlag){
	    	 retStr = '<img src="'+basePath+'/images/filemanager/attach_'+name+'.gif" alt="첨부파일">';
	     }else{
	    	 retStr = '<img src="'+basePath+'/images/filemanager/icon_doc.gif" alt="첨부파일">';
	     }
	     return retStr;
    }



    function sort(uploadFormId, command){


    	 var fileObj = {};
		 var tmpFileList =[];

		 if( $("#fileInfoList_"+uploadFormId).val() != ''){
			 fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
		}


    	var ordIndex=0;

    	var check=0;
    	 $("input[name=file_"+uploadFormId+"]").each(function(index){
    		  fileObj.fileInfo[index].selected=false;
    		  if($(this).is(":checked") ){
    			  check++;
    			  ordIndex = index;
    			  fileObj.fileInfo[index].selected=true;
    		  }
		 });

    	 if(check == 0){
    		 alert("항목을 선택하세요");
    		 return;
    	 }
    	 if(check!=1){
    		 alert("항목을 하나만 선택하세요");
    		 $("input[name=file_"+uploadFormId+"]").each(function(index){
    			  fileObj.fileInfo[index].selected=false;
				  $(this).prop('checked', false);
			 });
    		 return;
    	 }


		 if(command == 'up'){
			 if(ordIndex ==0) return;
			 fileObj.fileInfo.move(ordIndex,ordIndex-1);
		 }else if (command == 'down'){
			 if(ordIndex ==fileObj.fileInfo.length) return;
			 fileObj.fileInfo.move(ordIndex,ordIndex+1);
		 }else{
			 return;
		 }


		 $("#fileList_"+ uploadFormId).html("");
		 $("#fileInfoList_"+uploadFormId).val("");

		 fileObj.fileInfo.forEach(function(obj, index) {

			 displayFileList(obj, uploadFormId);

		 });

    }

    Array.prototype.move = function(from, to){
    	this.splice(to, 0, this.splice(from,1)[0]);
    }

    function isNotEmpty(str){
		 if(typeof str == 'undefined' || str == null || str==''){
			 return false;
		 }else{
			 return true;
		 }
	 }

    function isDefaultStr(str){
		 if(typeof str == 'undefined' || str == null || str==''){
			 return '';
		 }else{
			 return str;
		 }
	 }



    function checkAll(uploadFormId){
    	if( $("#checkAll_"+uploadFormId).prop("checked") ){
    		$("input[name=file_"+uploadFormId+"]").prop("checked",true);
    	}else{
    		$("input[name=file_"+uploadFormId+"]").prop("checked",false);
    	}
	 }

    function getFileInfo(obj){
		 if(typeof obj == 'undefined' || obj == null || obj==''){
			 return [];
		 }else{
			 return obj;
		 }
	 }



    function showFile(uploadFormId, popupId,url){

		 var showFile = [];
		 var fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );

		 var fileInfo ={};

		 $("input[name=file_"+uploadFormId+"]:checked").each(function(){

			 	var tmpFileId=$(this).val();

				 fileObj.fileInfo.forEach(function(obj, index) {
					 if(obj.fileId == tmpFileId){

						 fileInfo.fileId = obj.fileId;
						 fileInfo.docId= obj.docId;
						 fileInfo.temp = obj.temp;
						 fileInfo.fileNm= obj.fileNm;

						 showFile.push(fileInfo);
					 }
				 });

		 });

		 if(showFile.length==0){
			 alert("조회할 파일을 선택하세요");
			 return;
		 }

		 if(showFile.length >1 ){
			 alert("하나의 파일만 선택하세요");
			 return;
		 }


		 if(showFile[0].fileNm.lastIndexOf('.hwp') == -1 && showFile[0].fileNm.lastIndexOf('.hwpx') == -1){
		 		alert("hwp파일만 가능합니다.");
		 		return;
		 }


		 fileInfo.hwpPopupUrl=url;
		 openCustomPopup(popupId, '/file/hwpCtrlPopup', fileInfo);
	 }


    //첨부파일 내용조회
    function readFile(uploadFormId){
    	var readFile = [];
    	var fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val());
    	$("input[name=file_"+uploadFormId+"]:checked").each(function(){
    		var fileInfo ={};
    		var tmpFileId=$(this).val();

    		fileObj.fileInfo.forEach(function(obj, index) {
    			if(obj.fileId == tmpFileId){
    				fileInfo.fileId = obj.fileId;
    				fileInfo.docId= obj.docId;
    				fileInfo.temp = obj.temp;
    				fileInfo.fileNm= obj.fileNm;
    				fileInfo.delYn = 'Y';
    				readFile.push(fileInfo);
    			}
    		});
		 });

		 if(readFile.length==0){
			 alert("조회할 파일을 선택하세요");
			 return;
		 }

		 if(readFile.length >1 ){
			 alert("하나의 파일만 선택하세요");
			 return;
		 }

		 var fileInfoList = {};
		 fileInfoList.refDocId = "endDocId";
		 fileInfoList.fileInfo = readFile;
		 fileInfoList = JSON.stringify(fileInfoList);

		 $.ajax({
			 type : "post",
			 url : "/common/fileView/readFileAjax",
			 contentType:"application/x-www-form-urlencoded; charset=UTF-8",
			 data : {fileInfoList : fileInfoList},
			 success: function (result) {
				 var txt = result.fileText;
				 txt = txt.replace(/<!--[^>](.*?)-->/g, "");
				 txt = txt.replace(/\r\n/g, "<br>");
				 txt = txt.replace(/\n/g, "<br>");

				 CKEDITOR.instances.reply_cnts.setData(txt);  //ck editor 내용 입력

				 //displayFileList(readFile[0], uploadFormId);  //삭제시
			 }, error : function(jqXHR) {
				alert("예외가 발생했습니다. 관리자에게 문의하세요.");
			 }
		 });
	}


    function getFileExtension(filename) {
        return filename.split('.').pop().toLowerCase();
    }

    function getFileClass(extension) {
        var fileClasses = {
            'hwp': 'hwp',
            'hwpx': 'hwp',
            'doc': 'doc',
            'docx': 'doc',
            'pdf': 'pdf',
            'xls': 'xls',
            'xlsx': 'xls',
            'ppt': 'ppt',
            'pptx': 'ppt',
            'txt': 'txt',
            'zip': 'zip',
            'jpg': 'jpg',
            'jpeg': 'jpg',
            'png': 'png',
            'gif': 'gif',
            'xml': 'xml',
            'html': 'html',
            'mp4': 'mp4',
            'mpg': 'mpg',
            'tit': 'tit',
            'avi': 'avi',
            'exe': 'exe',
            'bmp': 'bmp'
        };

        return fileClasses[extension] || 'etc';
    }


    function checkedFileInfo(uploadFormId, openYn){
		 var checkedFileInfo='';
		 var fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
		 $("input[name=file_"+uploadFormId+"]:checked").each(function(){
			 	var tmpFileId=$(this).val();

				 fileObj.fileInfo.forEach(function(obj, index) {
					 if(obj.fileId == tmpFileId){
						 checkedFileInfo+=obj.fileId+",";
					 }
				 });

		 });

		 if(checkedFileInfo.endsWith(",")){
			 checkedFileInfo= checkedFileInfo.slice(0,-1);
		 }

		 toggleVisibility(uploadFormId, checkedFileInfo,openYn);

    }

/*

    function updateVisibility(uploadFormId, itemsStr, openYn) {


    	 console.log("itemsStr", itemsStr);
    	 console.log("openYn", openYn);

    	 const publicInput = $("#open_refDocId_" + uploadFormId);
         const privateInput = $("#noOpen_refDocId_" + uploadFormId);

        // 안전한 초기화 (빈 값 대비)
        let publicItems = (publicInput.val() || '')
            .split(',')
            .map(s => s.trim())
            .filter(Boolean);

        let privateItems = (privateInput.val()  || '')
            .split(',')
            .map(s => s.trim())
            .filter(Boolean);

        const items = itemsStr
            .split(',')
            .map(s => s.trim())
            .filter(Boolean);

        // Set으로 중복 제거
        const publicSet = new Set(publicItems);
        const privateSet = new Set(privateItems);

        items.forEach(item => {
            if (openYn === 'Y') {
                privateSet.delete(item);
                publicSet.add(item);
            } else if (openYn === 'N') {
                publicSet.delete(item);
                privateSet.add(item);
            }
        });


        // 다시 저장
        publicInput.val( Array.from(publicSet).join(','));
        privateInput.val( Array.from(privateSet).join(','));


    }

*/

    function toggleVisibility(uploadFormId, itemsStr,openYn) {

        const publicInput = $("#open_refDocId_" + uploadFormId);
        const privateInput = $("#noOpen_refDocId_" + uploadFormId);

        let publicItems = (publicInput.value||"").split(',').filter(Boolean);
        let privateItems = (privateInput.value||"").split(',').filter(Boolean);

        // 넘어온 itemsStr을 배열로 변환
        const items = itemsStr.split(',').map(s => s.trim()).filter(Boolean);

        items.forEach(item => {
            if (openYn=='Y') {
                // 공개 → 비공개
                privateItems = privateItems.filter(i => i !== item);
                if (!publicItems.includes(item)) publicItems.push(item);

            } else if (openYn=='N') {
                // 비공개 → 공개
            	 publicItems = publicItems.filter(i => i !== item);
                 if (!privateItems.includes(item)) privateItems.push(item);
            }
        });


		 $("#open_refDocId_" + uploadFormId).val(publicItems.join(','));
		 $("#noOpen_refDocId_" + uploadFormId).val( privateItems.join(','));


    }



	 return {
		 upload : upload,
		 deleteFile  : deleteFile,
		 deleteAllFile : deleteAllFile,
		 download : download,
		 allDownload : allDownload,
		 fileDownload : fileDownload,
		 sort : sort,
		 setFileDetail:setFileDetail,
		 checkAll : checkAll,
		 reload : reload,
		 showFile : showFile,
		 readFile : readFile,
		 checkedFileInfo :checkedFileInfo
	 };


})();


