<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<!DOCTYPE html>
<html lang="ko">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>이동중</title>
		<%@ include file="/WEB-INF/jsp/framework/_includes/includeScript.jspf" %>
		<script type="text/javascript">
			<% pageContext.setAttribute("newLine", "\n"); %>
			<c:if test="${not empty message}">
				alert('<c:out value="${fn:replace(message,newLine,'\\\\n')}" />');
			</c:if>
			$(document).ready(function(){
				document.aform.action='${basePath}<c:url value="${redirectUrl}"/>';
				document.aform.submit();
			});
		</script>

	</head>
	<body>
		<form name="aform" method="${empty method ? 'POST':method}" target="${empty target ? '_self':target }">
			<c:forEach var="par" items="${paramValues}">
				  <input type="hidden" name="${par.key}" value="${par.value[0]}"/>
			</c:forEach>
		</form>
	</body>
</html>



