<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<!DOCTYPE html>
<html lang="ko">
	<head>
		<meta charset="utf-8">
		<%@ include file="/WEB-INF/jsp/framework/_includes/includeScript.jspf" %>

		<script type="text/javascript">
		<c:if test="${not empty message}">
		alert('<c:out value="${message}" />');
		window.close();
		</c:if>
		</script>
	</head>
	<body></body>
</html>
