<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

	<script>

		$(document).ready(function() {

			var f=$(this).attr('data-auth-chk');
			console.log(f);
			$('select').select2({

			})

			//엔터키 검색
			$("#schUserNm").on('keypress', function(e){
				if(e.keyCode == 13){
					e.preventDefault();
					$('#search').click();
				}
			});

		


			$.datepicker.setDefaults($.datepicker.regional['ko']);
			$( ".datepicker" ).datepicker({
				changeYear: true,
				changeMonth: true,
				 nextText: '다음 달',
				 prevText: '이전 달',
				 dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
				 dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
				 monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
				 monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
				 dateFormat: "yy-mm-dd"
			});
		})



		//검색 조회
		 function goSearch(){

			document.listForm.pageNo.value = 1;
			document.listForm.action = "${basePath}/admin/user/selectUserList";
			document.listForm.submit();
		 }

		 /* 검색조건 초기화 */
			function reset() {
				$("form[name=listForm]").find("input[type=text][name^=sch]").val('');
		 		$('#schOrgCd').val("").select2();
				$('#schRoleSeq').val("").select2();
				$('#schUserStatsCd').val("").select2();

		 	}


	</script>

	<section id="section" class="section">
	    <div class="main-cont-box">
	    	<form id="listForm" name="listForm" action="${basePath}/admin/user/selectUserList" method="post" onkeypress="javascript:if(event.keyCode == '13') goSearch()">
	    		<!--  메뉴, 페이징 파라메터-->
				<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true" />
				<input type="hidden" name="userId" id="userId" value="" >
				<input type="hidden" name="userNo" id="userNo" value="" >
				

		        <h2 class="page-title"><span>사용자관리</span></h2>
		        <blockquote class="page-desc">사용자정보를  조회할 수 있습니다.</blockquote>
		        <div class="detail-search">
			                <div class="detail-search mT20">
			                    <table class="table border">
			                        <colgroup>
			                            <col style="width: 15%">
			                            <col style="width: 38%">
			                            <col style="width: 15%">
			                            <col style="width: 38%">
			                        </colgroup>
			                        <tbody>
			                            <tr>
			                                <th><label for="schUserNm">이름</label></th>
			                                <td>
			                                    <div class="inputbox">
			                                        <input type="text" class="input-s" id="schUserNm" name="schUserNm" value="${keyword.schUserNm}">
			                                    </div>
			                                </td>
			                                <th><label for="schUserId">아이디</label></th>
			                                <td>
			                                	<div class="inputbox">
			                                		<input type="text" class="input-s" id="schUserId" name="schUserId" value="${keyword.schUserId }">
			                                	</div>
			                                </td>
			                            </tr>
			                            <tr>
			                                <th><label for="schOrgCd">부처 </label></th>
			                                <td>
			                                	<div class="selectbox">
				                                	<select name="schOrgCd" id="schOrgCd" class="select" multiple="multiple" onchange="multiCheck();">
				                                		<c:forEach items="${orgList}" var="orgList" varStatus="status">
				                                			<option value="${orgList.orgCd}">${orgList.hghrnkOrgNm}</option>
				                                		</c:forEach>
				                                	</select>
			                                	</div>
			                                </td>
			                                <th><label for="schUpdtDttm">수정일</label></th>
			                                <td>
			                                	<div class="inputbox">
			                                		<div class="inputbox-align">
					                                	<input type="text" id="schUpdtDttm" name="schUpdtDttm" class="input datepicker" value="${keyword.schUpdtDttm}" autocomplete="off" readonly="readonly">
														<img src="${basePath}/img/ico_calendar.png" alt="달력 아이콘" class="calendar">
													</div>
												</div>
			                                </td>
			                            </tr>
			                            
			                        </tbody>
			                    </table>
			                    <div class="list-back">
			                        <ul class="btn-list">
			                            <li><a href="javascript:goSearch();" class="btn-submit-s navy">검색</a></li>
			                            <li><a href="javascript:reset();" class="btn-submit-s gray btn-close">초기화</a></li>
			                        </ul>
			                    </div>
			               </div>
			        </div>
			        <!-- 게시판 리스트 -->
			        <div class="table-scroll">
			            	<div class="table">
			                	<table class="table">
				                	 <colgroup>
			                            <col style="width: 7%">
			                            <col style="width: 7%">
			                            <col style="width: 7%">
			                            <col style="width: 15%">
			                            <col style="width: 15%">
			                            <col style="width: 15%">
			                            <col style="width: 8%">
			                            <col style="width: 8%">
			                            <col style="width: 8%">
				                      </colgroup>
					                    <thead>
					                        <tr>
					                            <th>번호</th>
					                            <th>아이디</th>
					                            <th>이름</th>
					                            <th>업무권한</th>
					                            <th>부처(기관)</th>
					                            <th>부서</th>
					                            <th>사용상태</th>
					                            <th>등록일</th>
					                            <th>수정일</th>
					                        </tr>
					                    </thead>
					                    <tbody>
				                        	<c:forEach items="${list}" var="result">
				                        		<tr>
				                        			<td>
			                        					<c:out value="${result.userNo}" />
				                        			</td>

				                        			<td><c:out value="${result.userId}" /></td>

				                        			<td class="center title"><a href="javascript:detailUser('${result.userNo}')"><span class="tit">${result.userNm}</span></a></td>
				                        			<td>
				                        				<c:forEach items="${result.roleSeq}" var="roleSeq">
				                        					 <c:out value="${roleCode[''.concat(roleSeq)]}" />
				                        					 <br>
				                        				</c:forEach>
				                        			</td>

				                        			<td>
														<c:out value="${result.hghrnkOrgNm}" />
													</td>

													<td>
														<c:out value="${result.lwrnkOrgNm}" />
													</td>

					                        		<td>
														<c:out value="${result.userStatsNm}" />
													</td>

					                        		<td>
														<fmt:formatDate value="${result.rgstDttm}" pattern="yyyy-MM-dd"/>
													</td>
													<td>
														<fmt:formatDate value="${result.updtDttm}" pattern="yyyy-MM-dd"/>
													</td>
					                        	</tr>
				                        	</c:forEach>
				                        	<c:if test="${empty list}" >
					                        	<tr>
					                        		<td colspan="9" style="text-align: center;">검색 결과가 없습니다.</td>
					                        	</tr>
				                        	</c:if>
					                    </tbody>
					                </table>
					            </div>
					        </div>
				            <jsp:include page="/WEB-INF/jsp/framework/_includes/paging.jsp" flush="true" />
				            <div class="button" style="text-align: right;">
								<button type="button" class="btn-submit-s" onclick="javascript:userForm();">등록</button>
							</div>
			       	</form>
			</div>
		<div id = "popUserForm" class="popup-box2"></div>
		<div id ="dimF" style="display: none; position: fixed; top: 0; left: 0; right: 0; bottom: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); z-index: 5;"></div>
	</section>
















