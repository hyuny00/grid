<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<script src="${basePath}/js/jquery/jquery-3.7.1.min.js"></script>
<script  src="${basePath}/js/file/fileupload.js"></script>

<script>

var token;
$(document).ready(function(){
	  token =  $('input[name="_csrf"]').attr('value');
	  $.ajaxSetup({
		    beforeSend: function(xhr, settings) {
		    	xhr.setRequestHeader("AJAX", true);
		    	//jwt 사용시 csrf사용안하게 함
		        if (!csrfSafeMethod(settings.type) && !this.crossDomain) {
		            xhr.setRequestHeader("${_csrf.headerName}", token);
		        }
		    }
		});
});

function csrfSafeMethod(method) {
   return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
}


</script>

 <jsp:include page="/file/uploadForm" flush="true">
       <jsp:param name="refDocId" value="${param.refDocId}"/>
       <jsp:param name="docId" value="${param.docId}"/>
       <jsp:param name="mode" value="${param.mode}"/>
       <jsp:param name="maxFileSize" value="${param.maxFileSize}"/>
 </jsp:include>



