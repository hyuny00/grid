

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
    function fileDropDown(obj){

   	 	var uploadFormId = $(obj).data("uploadFormId");
        var dropZone = $("#fileList_"+uploadFormId);

        //Drag기능
        dropZone.on('dragenter',function(e){
            e.stopPropagation();
            e.preventDefault();
            dropZone.css('background-color','#E3F2FC');
        });
        dropZone.on('dragleave',function(e){
            e.stopPropagation();
            e.preventDefault();
            dropZone.css('background-color','#FFFFFF');
        });
        dropZone.on('dragover',function(e){
            e.stopPropagation();
            e.preventDefault();
            dropZone.css('background-color','#E3F2FC');
        });
        dropZone.on('drop',function(e){

        	e.preventDefault();
        	dropZone.css('background-color','#FFFFFF');

        	var disabled= $("#file_input_"+uploadFormId).attr('disabled');

        	if(isDefaultStr(disabled) == ''){

                var files = e.originalEvent.dataTransfer.files;
                if(files != null){
                    if(files.length < 1){
                        return;
                    }
            		 var fileList = getFileArray(uploadFormId, files);
                     setUploadInfo(fileList, uploadFormId)
                }else{
                    alert("ERROR");
                }
        	}else{
        		alert("업로드 중에는 사용하실수 없습니다.");
        	}

        });
    }


	//업로드 정보세팅
	 function setUploadInfo(fileList, uploadFormId){

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
		 var tmpFileList =[];


		 if( $("#fileInfoList_"+uploadFormId).val() != ''){
			 fileObj =  JSON.parse($("#fileInfoList_"+uploadFormId).val() );
			 tmpFileList = fileObj.fileInfo;

			 if(fileInfo.fileNm!=''){
				 $("#fileList_img_"+uploadFormId).html("");
			 }

		 }else{
			 $("#fileList_"+uploadFormId).html("");
		 }



		 if(fileInfo.delYn == 'N'){


			var found= false;
			tmpFileList.forEach(function(item,index){
				if(item.fileId == fileInfo.fileId){
					found=true;
				}
			});

			if(!found){

				 tmpFileList.push(fileInfo);
				 fileObj.refDocId=$("#refDocId_"+uploadFormId).val();
				 fileObj.docId=$("#docId_"+uploadFormId).val();
				 fileObj.fileInfo=tmpFileList;
				 var fileNm = fileInfo.fileNm;

		         var checked="";
		         if(fileInfo.selected){
		        	 checked="checked";
		         }


		         var requiredAttachIndex   =  $("#fileList_"+uploadFormId).data("requiredAttachIndex");
		         if(requiredAttachIndex!=''){
	        	    var refDocId=$("#refDocId_"+uploadFormId).val();
	        	    $("#"+refDocId+"_"+requiredAttachIndex).val("Y");
		         }

		          var str='<a href="javascript:uploadModule.fileDownload(\''+uploadFormId+'\', \''+fileInfo.fileId+'\', \''+fileInfo.fileNm+'\',\''+fileInfo.temp+'\');">'+fileNm+'['+Math.round(Number(fileInfo.fileSize/1024))+'k]</a>';


		          var fileFunStr='uploadModule.setFileDetail("'+fileInfo.docId+'","'+fileInfo.fileId+'", "'+fileInfo.fileNm+'");';


		          if($("#refDocId_"+uploadFormId).val()=="NONE"){
		        	  $("#fileList_"+ uploadFormId).append("<li><span>"+fnFileImg(fileNm)+str+"</span></li>");

		          }else{
		        	  $("#fileList_"+ uploadFormId).append("<li><div data-file-Id='"+fileInfo.fileId+"' style='padding-left:8px'><input onclick='"+fileFunStr+"' class='check' id='"+random+"-"+fileInfo.fileId+"' type='checkbox' "+ checked+"  name='file_"+uploadFormId+"' value='"+fileInfo.fileId+"'> <label class='label' for='"+random+"-"+fileInfo.fileId+"'><span>"+fnFileImg(fileNm)+str+"</span></label></div></li>");
		          }

				 // $("#fileList_"+ uploadFormId).append("<div data-file-Id='"+fileInfo.fileId+"' style='padding-left:8px'><input type='checkbox' "+ checked+" name='file_"+uploadFormId+"' value='"+fileInfo.fileId+"'><span style='padding-left:10px'>"+fnFileImg(fileNm)+"</span>"+str+"</div>");



			}
		 }else{

			 $("#fileList_"+uploadFormId+" > li > div").each(function() {
				 if( $(this).data('fileId') == fileInfo.fileId && fileInfo.delYn== 'Y'){
					 $(this).remove();
				 }

			 });



			 fileObj.fileInfo.forEach(function(obj, index) {
				 if(obj.fileId == fileInfo.fileId && fileInfo.delYn== 'Y'){
					//삭제시 삭제정보가 필요할경우 (저장시삭제)

					 if(obj.temp == 'Y'){
						 fileObj.fileInfo.splice(index,1);
					 }else{
						 obj.delYn = 'Y';
					 }


					//삭제시 바로 삭제할경우
					// fileObj.fileInfo.splice(index,1);


				 }


			 });

			 if(fileObj.fileInfo.length==0){

				 var requiredAttachIndex   =  $("#fileList_"+uploadFormId).data("requiredAttachIndex");
				 if(requiredAttachIndex!=''){
					 var refDocId=$("#refDocId_"+uploadFormId).val();
					 $("#"+refDocId+"_"+requiredAttachIndex).val("");
				 }
			 }


		 }

		 $("#fileInfoList_"+uploadFormId).val(JSON.stringify(fileObj));

		 /*
		 if($("#fileList_"+uploadFormId).children().length==0){
			 $("#delButton_"+uploadFormId).hide();
		 }else{
			 $("#delButton_"+uploadFormId).show();
		 }


		 if($("#fileList_"+uploadFormId).children().length > 1){
			 $("#sortButton_"+uploadFormId).show();
		 }else{
			 $("#sortButton_"+uploadFormId).hide();
		 }
*/

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
				 $("#progressLabel_"+uploadFormId).append("["+fileNm+"] : 파일확장자가 "+fileExt+ "인 파일은 업로드 할수 없습니다.");
				 continue;
			 }

			 var tmpmaxFileSize;
			 if( isNotEmpty(maxFileSize)){
				 tmpmaxFileSize = maxFileSize.substr(0, maxFileSize.length-1);
				 if(files[i].size > tmpmaxFileSize*1024*1024 ){
					 $("#progressLabel_"+uploadFormId).append("["+fileNm+"] : 파일사이즈가 "+maxFileSize+ "이하만 업로드 가능합니다.");
					 continue;
				 }
			 }else{
				 tmpmaxFileSize = 0;
			 }


			 if( tempFileList.indexOf(files[i].name) != -1){
				 $("#progressLabel_"+uploadFormId).append("["+fileNm+"] : 파일명이 같은 파일이 존재합니다.");
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



    function showFile(uploadFormId){

		 var showFile = [];
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

		 hwpCtrlPopup(showFile[0].fileId,showFile[0].fileNm,showFile[0].temp);
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
		 readFile : readFile
	 };


})();


