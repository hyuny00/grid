<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<!DOCTYPE html>
<html lang="ko">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>이동중</title>
		<%@ include file="/WEB-INF/jsp/framework/_includes/includeScript.jspf" %>
		<script type="text/javascript">
			$(document).ready(function(){
				 document.logoutForm.submit();
			});
		</script>
	</head>
	<body>
		<form id="logoutForm" name="logoutForm" action="${basePath}/logout" method="POST">
			<input type="hidden" name="${ _csrf.parameterName }" value="${ _csrf.token }">
		</form>

	</body>
</html>



