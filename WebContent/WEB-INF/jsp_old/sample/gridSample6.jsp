<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<style>


</style>


<script type="text/javascript">


 
</script>

<section id="section" class="section">
    <div class="main-cont-box">
        <div class="rightcolumn">
            
            
            
		
		<!-- JSP 인클루드 파일들 (필요시) -->
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
		<jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>
		
		<div>
    <button id="error">error</button>
    <button id="warning">warning</button>
    <button id="success">success</button>
    <button id="info">info</button>
</div>
 
<script type="text/javascript">
   
 
    $('#error').on('click', function(){
        toastr["error"]('error');
    });
    $('#warning').on('click', function(){
        toastr["warning"]('warning');
    });
    $('#success').on('click', function(){
    	 toastr["success"]("Message", "Title");
    });
    $('#info').on('click', function(){
        toastr["info"]('info');
    });
    
   
    </script>
            
            
            
        </div>
    </div>
</section>


