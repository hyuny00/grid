<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
    <!--For Commons Validator Client Side-->

    <script type="text/javaScript" >

        /* 글 목록 화면 function */
        function sampleList() {
           	document.detailForm.action = "${basePath}/sample/selectSampleListForm";
           	document.detailForm.submit();
        }

        /* 글 삭제 function */
        function deleteSample() {
           	document.detailForm.action = "${basePath}/sample/deleteSample";
           	document.detailForm.submit();
        }

        /* 글 등록 function */
        function insertSample() {
        	frm = document.detailForm;

            	frm.action = "${basePath}/sample/insertSample";
                frm.submit();

        }

        function updateSample() {
        	frm = document.detailForm;

            	frm.action = "${basePath}/sample/updateSample";
                frm.submit();

        }


        function closePopup(){
        	$("#popLayer").hide();

        }



    



    </script>
</head>
<section id="section" class="section">
  <div class="main-cont-box">
<div class="rightcolumn">
<form id="detailForm" method="post" name="detailForm">

	<input type="hidden" name="deletefileGroupId" value="${result.attcDocId}|${result.attcDocId2}"/>
	
	<!-- 검색조건, 메뉴, 페이징 파라메터-->
	<jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
	<jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>

	

    <div id="content_pop">
    	<!-- 타이틀 -->
    	<div id="title">

    	</div>
    	<!-- // 타이틀 -->
    	<div id="table">
    	<table width="70%" border="1" cellpadding="0" cellspacing="0" style="bordercolor:#D3E2EC; bordercolordark:#FFFFFF; BORDER-TOP:#C2D0DB 2px solid; BORDER-LEFT:#ffffff 1px solid; BORDER-RIGHT:#ffffff 1px solid; BORDER-BOTTOM:#C2D0DB 1px solid; border-collapse: collapse;">
    		<colgroup>
    			<col width="150"/>
    			<col width="?"/>
    		</colgroup>
        		<tr>
        			<td class="tbtd_content">
        				ID
        			</td>
        			<td class="tbtd_content">
        				<input type="text" name="id" value="${result.id}" cssClass="essentiality" maxlength="10"  />
        			</td>
        		</tr>
    		<tr>
    				<td class="tbtd_content">
        				NAME
        			</td>
    			<td class="tbtd_content">
    				<input type="text" name="name" value="${result.name}" maxlength="30" cssClass="txt"/>
    			</td>
    		</tr>
    		<tr>
    			<td class="tbtd_caption">useYn</td>
    			<td class="tbtd_content">
    				<select name="useYn" cssClass="use">
    					<option value="Y" >Yes</option>
    					<option value="N">No</option>
    				</select>
    			</td>
    		</tr>
    		<tr>
    			<td class="tbtd_caption">description</td>
    			<td class="tbtd_content">
    				<textarea name="description"  rows="5" cols="58" >${result.description}</textarea>
                </td>
    		</tr>
    		<tr>
    			<td class="tbtd_caption"><label for="regUser">regUser</label></td>
    			<td class="tbtd_content">

        				<input  type="text" name="regUser" value="${result.regUser}" maxlength="10" cssClass="txt"  />
        				</td>

    		</tr>

    		<tr>
	    		<td class="tbtd_caption">파일</td>
	    		<td class="tbtd_content">
	    		<!-- refDocId (필수) : 문서아이디 :  DB 컬럼명 -->
	    		<!-- docId (필수) : 문서아이디 : DB 컬럼값 -->
	    		<!-- acceptType(선택) :[doc:문서파일만,image : 이미지파일만,multimedia :멀팀미디어] -->
				<!-- mode (선택) :[view:조회] -->
				<!-- singleFile(선택)  : [Y:파일하나만 업로드] -->
				<!-- maxFileCnt(선택)  : [업로드 갯수제한] -->
				<!-- maxFileSize (선택) : 단위(M) 업로드 제한, 시스템 디폴트 이상으로세팅하면 시스템 디폴트로 제한 -->
				<!-- requiredAttachIndex : 필수입력인경우 세팅. 업로드폼여러개할경우 각자 다른값으로 세팅
				  	 필수체크  $("#attcDocId_1").val()=='Y'  이면 파일등록되어있음
				-->
		    		  <jsp:include page="/file/uploadForm" flush="true">
					   	<jsp:param name="refDocId" value="attcDocId"/>
					  	<jsp:param name="docId" value="${result.attcDocId}"/>
					  	<jsp:param name="maxFileSize" value="20M"/>
					  	<jsp:param name="arrButton" value="Y"/>
					  	
					  </jsp:include>



				  </td>
			</tr>
			
			
    		<tr>
	    		<td class="tbtd_caption">파일2</td>
	    		<td class="tbtd_content">
	    		<!-- refDocId (필수) : 문서아이디 :  DB 컬럼명 -->
	    		<!-- docId (필수) : 문서아이디 : DB 컬럼값 -->
	    		<!-- acceptType(선택) :[doc:문서파일만,image : 이미지파일만,multimedia :멀팀미디어] -->
				<!-- mode (선택) :[view:조회] -->
				<!-- singleFile(선택)  : [Y:파일하나만 업로드] -->
				<!-- maxFileCnt(선택)  : [업로드 갯수제한] -->
				<!-- maxFileSize (선택) : 단위(M) 업로드 제한, 시스템 디폴트 이상으로세팅하면 시스템 디폴트로 제한 -->
				<!-- requiredAttachIndex : 필수입력인경우 세팅. 업로드폼여러개할경우 각자 다른값으로 세팅
				  	 필수체크  $("#attcDocId_1").val()=='Y'  이면 파일등록되어있음
				-->
		    		  <jsp:include page="/file/uploadForm" flush="true">
					   	<jsp:param name="refDocId" value="attcDocId2"/>
					  	<jsp:param name="docId" value="${result.attcDocId2}"/>
					  	<jsp:param name="maxFileSize" value="20M"/>
					  </jsp:include>



				  </td>
			</tr>

			<tr>
	    		<td class="tbtd_caption">파일조회</td>
	    		<td class="tbtd_content">
						<!-- mode (필수) :[view:조회] -->
						<!-- docId (필수) : 문서아이디 : DB 컬럼값 -->
						<!--viewType(선택) -> H :가로, V: 세로(디폴트) -->
		    		  <jsp:include page="/file/uploadForm" flush="true">
					  	 <jsp:param name="docId" value="${result.attcDocId}"/>
					  	 <jsp:param name="mode" value="view"/>
					  	 <jsp:param name="viewType" value="H"/>
					  </jsp:include>
				  </td>
			</tr>
			
			
			<tr>
	    		<td class="tbtd_caption">파일조회2</td>
	    		<td class="tbtd_content">
						<!-- mode (필수) :[view:조회] -->
						<!-- docId (필수) : 문서아이디 : DB 컬럼값 -->
						<!--viewType(선택) -> H :가로, V: 세로(디폴트) -->
		    		  <jsp:include page="/file/uploadForm" flush="true">
					  	 <jsp:param name="docId" value="${result.attcDocId2}"/>
					  	 <jsp:param name="mode" value="view"/>
					  	 <jsp:param name="viewType" value="V"/>
					  </jsp:include>
				  </td>
			</tr>
			
			

    	</table>
      </div>
    	<div id="sysbtn">
    		<ul>
    			<li>
                    <span class="btn_blue_l">
                        <a href="javascript:sampleList();"></a>
                    </span>
                </li>
    			<li>
                    <span class="btn_blue_l">
                        <a href="javascript:insertSample();">
                            create
                        </a>
                    </span>
                </li>

                <li>
                    <span class="btn_blue_l">
                         <a href="javascript:updateSample();">
                            update
                        </a>
                    </span>
                </li>
                    <li>
                        <span class="btn_blue_l">
                            <a href="javascript:deleteSample();">delete</a>
                        </span>
                    </li>

                      <li>
                        <span class="btn_blue_l">
                            <a href="javascript:sampleList();">list</a>
                        </span>
                    </li>
    			<li>
                    <span class="btn_blue_l">
                        <a href="javascript:document.detailForm.reset();">reset</a>
                    </span>
                </li>
            </ul>
    	</div>
    </div>

</form>
</div>

</div>

<div id="popLayer" style="display:none;width:1500px;height:1000px;border:4px solid #ddd;background:#fff;"> </div>
</section>


