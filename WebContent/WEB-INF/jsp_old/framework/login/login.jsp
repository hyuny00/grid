<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeScript.jspf" %>
<html>
<head>
	<title>Home</title>
</head>
<script>
      	if("${errorMsg}" != ''){
      		alert("${errorMsg}");
      	}
</script>
<body>
<h2>로그인 </h2>
<form name="form1" method="post" action="${basePath}/loginSessAndJwt">
<input type="hidden" name="${ _csrf.parameterName }" value="${ _csrf.token }">
<table>
    <tr height="40px">
        <td>유저ID</td>
        <td><input type="text" name="userId"  value="admin"></td>
    </tr>
    <tr height="40px">
        <td>패스워드</td>
        <td><input type="password" name="password" value="admin"></td>
    </tr>
</table>
<table>

    <tr>
        <td align="center"><input type="submit" value="로그인"></td>
        <td align="center"><input type="reset" value="리셋"></td>
    </tr>
</table>
</form>
</body>
</html>
