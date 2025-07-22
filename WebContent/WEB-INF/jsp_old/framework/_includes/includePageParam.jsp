<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<input type="hidden" name="topMenuSeq" value="${param.topMenuSeq}" id="topMenuSeq"/>
<input type="hidden" name="menuSeq" value="${param.menuSeq}" id="menuSeq" />
<input type="hidden" name="upMenuSeq" value="${param.upMenuSeq}" id="upMenuSeq"/>

<%--
<c:forEach items="${entry}" var="entry">
		<input type="hidden" name="${entry.key}" value="${entry.value}"/>
</c:forEach>



<input type="hidden" name="pageNo" value="<c:if test="${empty param.pageNo}">1</c:if><c:if test="${not empty param.pageNo}">${param.pageNo}</c:if>"/>
<input type="hidden" name="pageSize" value="<c:if test="${empty param.pageSize}">10</c:if><c:if test="${not empty param.pageSize}">${param.pageSize}</c:if>"/>

--%>
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
