<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

 <script>
 		function goPage(pageNo){
	    	var formName= $("#paging").closest('form').attr('name');

	    	document[formName].pageSize.value=document[formName].listPageSize.value;
	    	document[formName].pageNo.value = pageNo;
	       	document[formName].submit();
	    }

	    function changePageSize(){
	    	var formName= $("#paging").closest('form').attr('name');

	    	document[formName].pageSize.value=document[formName].listPageSize.value;
	    	document[formName].pageNo.value = 1;
	       	document[formName].submit();
	    }
  </script>



 <div  id="paging"  class="paging">
     <ul>
     	<c:if test="${pageObject.prev}">
	         <li class="first"><a href="javascript:goPage(1)">처음</a></li>
	         <li class="prev"><a href="javascript:goPage(${pageObject.beginPage-1})">이전</a></li>
         </c:if>

         <c:forEach begin="${pageObject.beginPage}" end="${pageObject.endPage}" step="1" var="index">
		    <c:choose>
		        <c:when test="${pageObject.pageNo==index}">
		            <li class="active"><a href="#">${index}</a></li>
		        </c:when>
		        <c:otherwise>
		            <li><a href="javascript:goPage(${index})">${index}</a></li>
		        </c:otherwise>
		    </c:choose>
		</c:forEach>

		<c:if test="${pageObject.endPage eq '0' }">
			<li class="active">&nbsp;</li>
		</c:if>

         <c:if test="${pageObject.next}">
		     <li class="next"><a href="javascript:goPage(${pageObject.endPage+1})">다음</a></li>
		    <li class="last"><a href="javascript:goPage(${pageObject.totalPage})">마지막</a></li>
		</c:if>
     </ul>
     <div class="search-box2">
         <div class="all-search">총 <span class="num">${pageObject.totalCount}</span>건</div>
         <div class="selectbox">
			<label for="listPageSize"></label>
            <select id="listPageSize" name="listPageSize" onChange="javascript:changePageSize()" class="select-s w100">
                 <option value="10" <c:if test="${pageObject.pageSize eq '10'}">selected</c:if>>10</option>
                 <option value="20" <c:if test="${pageObject.pageSize eq '20'}">selected</c:if>>20</option>
                 <option value="50" <c:if test="${pageObject.pageSize eq '50'}">selected</c:if>>50</option>
                 <option value="100" <c:if test="${pageObject.pageSize eq '100'}">selected</c:if>>100</option>
                 <option value="500" <c:if test="${pageObject.pageSize eq '500'}">selected</c:if>>500</option>
             </select>
         </div>
     </div>
 </div>