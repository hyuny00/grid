<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>NICE평가정보 - CheckPlus 안심본인인증 테스트(결과)</title>
</head>
<body>

<span>복호화 데이터</span>
<ul>
  	<c:forEach var="entry" items="${mapResult}">
	    <li>
	        <strong>${entry.key}</strong>: <span>${entry.value}</span>
	    </li>
	</c:forEach>
</ul>
</body>
</html>