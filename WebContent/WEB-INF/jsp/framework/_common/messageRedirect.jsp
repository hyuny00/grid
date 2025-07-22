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
			<c:set var="sendParams" value="${fn:split(sendParams, ',')}" />
			<c:forEach items="${sendParams}" var="item">
			 	<input type="hidden" name="${item}" value="${param[item] }"/>
			</c:forEach>
			<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
			<jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>
		</form>
	</body>
</html>
