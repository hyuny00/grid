<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes_old/includeTags.jspf" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>ODA통합정보포털</title>
	<link rel="stylesheet" href="${basePath}/css_old/reset.css">
	<link rel="stylesheet" href="${basePath}/css_old/style_admin.css">
	<!--  <link rel="stylesheet" href="${basePath}/css/thema02.css">-->
	<link rel="stylesheet" href="${basePath}/fonts_old/s_core_dream/stylesheet.css">
	<link rel="stylesheet" href="${basePath}/css_old/all.min.css">
	<%@ include file="/WEB-INF/jsp/framework/_includes_old/includeCss.jspf" %>
	<%@ include file="/WEB-INF/jsp/framework/_includes_old/includeScript.jspf" %>
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

