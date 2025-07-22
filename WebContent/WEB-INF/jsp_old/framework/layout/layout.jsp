<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>ODA통합정보포털</title>
	<link rel="stylesheet" href="${basePath}/css/reset.css">
	<!--<link rel="stylesheet" href="${basePath}/css/style.css">  -->
	<link rel="stylesheet" href="${basePath}/css/style_admin.css">
	<link rel="stylesheet" href="${basePath}/fonts/s_core_dream/stylesheet.css">
	<link rel="stylesheet" href="${basePath}/css/all.min.css">
	<%@ include file="/WEB-INF/jsp/framework/_includes/includeCss.jspf" %>
	<%@ include file="/WEB-INF/jsp/framework/_includes/includeScript.jspf" %>
</head>
<body>
<div id="wrap">

    <div class="wrap">
    	<tiles:insertAttribute name="leftMenu" ignore="true"/>
    	<div class="right-conts">
	        <tiles:insertAttribute name="header" />
	        <tiles:insertAttribute name="body" ignore="true"/>
	        <tiles:insertAttribute name="footer" ignore="true"/>
   		</div>
    </div>
</div>
</body>
</html>