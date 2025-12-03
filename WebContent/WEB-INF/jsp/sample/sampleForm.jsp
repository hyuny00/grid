<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>

<script type="text/javascript">
function sampleList() {
    document.detailForm.action = "${basePath}/sample/selectSampleListForm";
    document.detailForm.submit();
}

function deleteSample() {
    document.detailForm.action = "${basePath}/sample/deleteSample";
    document.detailForm.submit();
}

function insertSample() {
    document.detailForm.action = "${basePath}/sample/insertSample";
    document.detailForm.submit();
}

function updateSample() {
    document.detailForm.action = "${basePath}/sample/updateSample";
    document.detailForm.submit();
}

function closePopup(){
    $("#popLayer").hide();
}
</script>






			<div class="pgtBox">
					<div class="lt">
						<h2>자체평가 평가계획 등록 </h2>
					</div>

					<ul class="breadcrumb">
						<li class="home"><a href="javascript:;">홈</a></li>
						<li><a href="javascript:;">평가</a></li>
						<li><a href="javascript:;">자체평가</a></li>
					</ul>

					<div class="rt"></div>
				</div>

				<div class="titBox">
					<div class="lt">
						<h3>기본정보</h3>
					</div>

					<div class="rt">

					</div>
				</div>

			<form id="detailForm" method="post" name="detailForm">
                <input type="hidden" name="deletefileGroupId" value="${result.attcDocId}|${result.attcDocId2}" />
                <jsp:include page="/WEB-INF/jsp/framework/_includes/includePageParam.jsp" flush="true"/>
                <jsp:include page="/WEB-INF/jsp/framework/_includes/includeSchParam.jsp" flush="true"/>

                <div class="tblBox">
                    <table class="tbl col data">
                        <colgroup>
                            <col style="width: 20%;" />
                            <col />
                        </colgroup>
                        <tbody>
                            <tr>
                                <th scope="row">ID</th>
                                <td><input type="text" name="id" value="${result.id}" maxlength="10" class="w100" /></td>
                            </tr>
                            <tr>
                                <th scope="row">NAME</th>
                                <td><input type="text" name="name" value="${result.name}" maxlength="30" class="w100" /></td>
                            </tr>
                            <tr>
                                <th scope="row">useYn</th>
                                <td>
                                    <select name="useYn" class="w50">
                                        <option value="Y" ${result.useYn eq 'Y' ? 'selected' : ''}>Yes</option>
                                        <option value="N" ${result.useYn eq 'N' ? 'selected' : ''}>No</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th scope="row">description</th>
                                <td><textarea name="description" rows="5" class="w100">${result.description}</textarea></td>
                            </tr>
                            <tr>
                                <th scope="row">regUser</th>
                                <td><input type="text" name="regUser" value="${result.regUser}" maxlength="10" class="w100" /></td>
                            </tr>
<%--
                            <tr>
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
                                <th scope="row">파일</th>
                                <td>
                                    <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="refDocId" value="attcDocId"/>
                                        <jsp:param name="docId" value="${result.attcDocId}"/>
                                    </jsp:include>
                                </td>
                            </tr>
                            <tr>
                                <th scope="row">파일2(ZIP파일만)2</th>
                                <td>
                                    <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="refDocId" value="attcDocId2"/>
                                        <jsp:param name="docId" value="${result.attcDocId2}"/>
                                        <jsp:param name="maxFileSize" value="20M"/>
                                         <jsp:param name="isOnlyZip" value="Y"/>
                                    </jsp:include>
                                </td>
                            </tr>



                            <tr>
                                <th scope="row">파일조회</th>
                                <td>
                                    <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="docId" value="${result.attcDocId}"/>
                                        <jsp:param name="mode" value="view"/>
                                        <jsp:param name="viewType" value="H"/>
                                    </jsp:include>
                                </td>
                            </tr>
                            <tr>
                                <th scope="row">파일조회2</th>
                                <td>
                                    <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="docId" value="${result.attcDocId2}"/>
                                        <jsp:param name="mode" value="view"/>
                                        <jsp:param name="viewType" value="V"/>
                                    </jsp:include>
                                </td>
                            </tr>
                            --%>
                        </tbody>
                    </table>
                </div>

                <div class="tblBox">
				    <table class="tbl row">
						<caption></caption>
						<colgroup>
						  <col style="width: 152px;">
						  <col style="width: calc(100% - 152px);">
						</colgroup>

						<tbody>
						  <tr>
							<th scope="row">서류명</th>
							<td>
								  <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="refDocId" value="attcDocId"/>
                                        <jsp:param name="docId" value="${result.attcDocId}"/>
                                         <jsp:param name="allDownload" value="Y"/>
                                          <jsp:param name="extractZipYn" value="Y"/>


                                    </jsp:include>

							</td>
						  </tr>
						</tbody>
					</table>
				</div>


                <div class="tblBox">
				    <table class="tbl row">
						<caption></caption>
						<colgroup>
						  <col style="width: 152px;">
						  <col style="width: calc(100% - 152px);">
						</colgroup>

						<tbody>
						  <tr>
							<th scope="row">서류명</th>
							<td>
								  <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="refDocId" value="attcDocId1"/>
                                        <jsp:param name="docId" value="${result.attcDocId1}"/>
                                   </jsp:include>
							</td>
						  </tr>
						</tbody>
					</table>
				</div>



				 <div class="tblBox">
				    <table class="tbl row">
						<caption></caption>
						<colgroup>
						  <col style="width: 152px;">
						  <col style="width: calc(100% - 152px);">
						</colgroup>

						<tbody>
						  <tr>
							<th scope="row">서류명</th>
							<td>
								  <jsp:include page="/file/uploadForm" flush="true">
                                        <jsp:param name="refDocId" value="attcDocId"/>
                                        <jsp:param name="docId" value="${result.attcDocId}"/>
                                          <jsp:param name="mode" value="view"/>
                                    </jsp:include>

							</td>
						  </tr>
						</tbody>
					</table>
				</div>



                <div class="btn-wrap mt-3">
                    <button type="button" class="btn btn-secondary" onclick="sampleList();">목록</button>
                    <button type="button" class="btn btn-primary" onclick="insertSample();">등록</button>
                    <button type="button" class="btn btn-primary" onclick="updateSample();">수정</button>
                    <button type="button" class="btn btn-danger" onclick="deleteSample();">삭제</button>
                    <button type="reset" class="btn btn-light">초기화</button>
                </div>
            </form>



	<div title="Data Download" id="preparing-file-modal" style="display: none;">
	    <div id="progressbar" style="width: 100%; height: 22px; margin-top: 20px;"></div>
	</div>

	<!-- 파일 로딩중인 이미지입니다. -->
	<div class="file-loading" id="processingBar">
		<img src="${basePath}/img/processing4.gif" alt="처리중">
	</div>

<!-- 팝업 레이어 -->
<div id="popLayer" style="display:none;width:1500px;height:1000px;border:4px solid #ddd;background:#fff;"></div>
