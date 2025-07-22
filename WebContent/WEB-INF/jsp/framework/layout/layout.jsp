<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>ODA</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    
    <link rel="stylesheet" href="${basePath}/css/flatpickr.min.css">
	<link rel="stylesheet" href="${basePath}/css/reset.css">
	<link rel="stylesheet" href="${basePath}/css/layout.css">
	<link rel="stylesheet" href="${basePath}/css/style.css">



	<%@ include file="/WEB-INF/jsp/framework/_includes/includeCss.jspf" %>
	<%@ include file="/WEB-INF/jsp/framework/_includes/includeScript.jspf" %>
</head>
<body>
    <div id="wrap" class="wrapper">
        <!-- 헤더 영역 -->
        <tiles:insertAttribute name="header" />
        
        <!-- 컨테이너 영역 -->
        <div id="container" class="container">
            <!-- LNB 영역 -->
            <tiles:insertAttribute name="leftMenu" />
            
            <!-- 컨텐츠 영역 -->
            <div class="contents">
                <tiles:insertAttribute name="body" />
            </div>
        </div>
        
        <!-- 푸터 영역 -->
        <tiles:insertAttribute name="footer" ignore="true"/>
    </div>
</body>
</html>