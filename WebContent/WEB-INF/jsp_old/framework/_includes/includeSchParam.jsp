<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
<c:forEach var="par" items="${paramValues}">
    <c:if test="${fn:startsWith(par.key, 'sch')}">
		  <input type="hidden" name="${par.key}" value="${par.value[0]}"/>
    </c:if>
</c:forEach>
