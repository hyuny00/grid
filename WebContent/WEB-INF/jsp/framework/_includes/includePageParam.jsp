<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<input type="hidden" name="topMenuSeq" value="${param.topMenuSeq}" id="topMenuSeq"/>
<input type="hidden" name="menuSeq" value="${param.menuSeq}" id="menuSeq" />
<input type="hidden" name="upMenuSeq" value="${param.upMenuSeq}" id="upMenuSeq"/>
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
