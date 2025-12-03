<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>NICE 평가정보 JAVA 예제파일 기관토큰 발급</title>
</head>
<body>
    <h2>남은 시간 (초)</h2>
    ${expires_in}
    <hr>
    <h2>기관 토큰</h2>
   ${access_token}
</body>
</html>