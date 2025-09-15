<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>



<c:if test="${param.isOnlyZip eq 'Y'}">
    <c:set var="accept" value=".zip" />
</c:if>

<c:if test="${param.mode eq 'view'}">

    <div class="file">

    	<div class="btn-wrap">
			 <c:if test="${param.allDownload eq 'Y'}">
					<button type="button" class="btn down"  onclick="uploadModule.allDownload('${uploadFormId}')">일괄 다운로드</button>
			 </c:if>
		</div>

        <div class="file-upload" data-file-list data-doc-id="${param.docId}" data-upload-form-id="${uploadFormId}" data-mode="${param.mode}" data-ref-doc-id="${param.refDocId}" data-base-path="${basePath}">
        	<!-- 파일이 없을 때 표시될 메시지 -->
			<p id="emptyMessage_${uploadFormId}" class="txtInfo lg" style="position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);color:#8A949E;white-space:nowrap;" >첨부파일이 없습니다.</p>
            <ul id="fileList_${uploadFormId}" data-max-file-size="${param.maxFileSize}" data-accept-file="${accept}" data-single-file="${param.singleFile}" data-max-file-cnt="${param.maxFileCnt}" "></ul>
        </div>

        <input type="hidden" name="fileInfoList" id="fileInfoList_${uploadFormId}" value='{"refDocId":"NONE", "attcDocId":"","docId":"","fileInfo":[]}'>
        <c:if test="${not empty param.refDocId}">
            <input type="hidden" name="${param.refDocId}" id="docId_${uploadFormId}" value="${param.docId}">
        </c:if>
        <input type="hidden" name="refDocId" id="refDocId_${uploadFormId}" value="NONE">
    </div>
</c:if>

<c:if test="${param.mode ne 'view'}">
    <div class="file">

		<div class="btn-wrap">

			<c:if test="${param.checkAll eq 'Y'}">
		    	<button type="button" class="btn txt"   name="checkAll" id="checkAll_${uploadFormId}" onclick="uploadModule.toggleSelectAllFiles('${uploadFormId}')">전체선택</button>
			</c:if>

			<c:if test="${param.noButton ne 'Y'}">
			    <button type="button" class="btn-plus" onclick="document.getElementById('file_input_${uploadFormId}').click();">
			        <span class="sr-only">추가</span>
			    </button>
			    <button type="button" class="btn-del" onclick="uploadModule.deleteFile('${uploadFormId}')">
			        <span class="sr-only">삭제</span>
			    </button>
			    <c:if test="${param.arrButton eq 'Y'}">
			        <button type="button" class="btn-sort-up" onclick="uploadModule.sort('${uploadFormId}','up')">
			            <span class="sr-only">위로</span>
			        </button>
			        <button type="button" class="btn-sort-down" onclick="uploadModule.sort('${uploadFormId}','down')">
			            <span class="sr-only">아래로</span>
			        </button>
			    </c:if>
			</c:if>


			<c:if test="${param.allDownload eq 'Y'}">
		    	<button type="button" class="btn down"  onclick="uploadModule.allDownload('${uploadFormId}')">일괄 다운로드</button>
			</c:if>

			<c:if test="${param.homepageOpenYn eq 'Y'}">
		    	<button type="button" class="btn txt"   onclick="uploadModule.homepageOpenYn('${uploadFormId}')">홈페이지 공개/비공개</button>
			</c:if>

			<c:if test="${param.iipsOpenYn eq 'Y'}">
		    	<button type="button" class="btn txt"   onclick="uploadModule.iipsOpenYn('${uploadFormId}')">통합정보포털 공개/비공개</button>
			</c:if>


			 <c:if test="${param.hwpEditor eq 'Y'}">
			 	<button type="button" class="btn hwp"  onclick="uploadModule.showFile('${uploadFormId}', '${param.hwpPopupId}', '${param.hwpPopupUrl}')">한글 로폼</button>
			 </c:if>
			  <c:if test="${param.preView eq 'Y'}">
			 	<button type="button" class="btn">미리보기</button>
			 </c:if>
		</div>


        <div class="file-upload" data-file-list data-doc-id="${param.docId}" data-upload-form-id="${uploadFormId}" data-mode="${param.mode}" data-ref-doc-id="${param.refDocId}" data-base-path="${basePath}" data-no-button="${param.noButton}" >

			<!-- 파일이 없을 때 표시될 메시지 -->
			<p id="emptyMessage_${uploadFormId}" class="txt-file" >첨부하실 파일을 마우스로 끌어오세요.</p>
			<ul id="fileList_${uploadFormId}" data-thumbnail-yn="${param.thumbnailYn}"   data-required-attach-index="${param.requiredAttachIndex}" data-max-file-size="${param.maxFileSize}" data-accept-file="${accept}" data-single-file="${param.singleFile}" data-max-file-cnt="${param.maxFileCnt}" data-homepage-open-yn="${param.homepageOpenYn}"  data-iips-open-yn="${param.iipsOpenYn}" class="file-list dragndrop" style="display:none;"></ul>

			<input type="file" id="file_input_${uploadFormId}" accept="${accept}" multiple onChange="uploadModule.upload('${uploadFormId}');" hidden/>
        </div>

        <input type="hidden" name="fileInfoList" id="fileInfoList_${uploadFormId}" value='{"refDocId":"${param.refDocId}", "attcDocId":"", "docId":"", "fileInfo":[]}'>
        <input type="hidden" name="${param.refDocId}" id="docId_${uploadFormId}" value="${param.docId}">
        <input type="hidden" name="refDocId" id="refDocId_${uploadFormId}" value="${param.refDocId}">

        <input type="hidden" name="open" id="open_refDocId_${uploadFormId}" value="">
        <input type="hidden" name="noOpen" id="noOpen_refDocId_${uploadFormId}" value="">

        <c:if test="${not empty param.requiredAttachIndex}">
            <input type="hidden" name="${param.refDocId}_${param.requiredAttachIndex}" id="${param.refDocId}_${param.requiredAttachIndex}" value="">
        </c:if>
    </div>
			<!--
			<c:if test="${param.odaOpenYn eq 'Y'}">
    									<td>
											<div class="inputForm">
												<p>ODA KOREA 홈페이지</p>
												<div class="rdoBox">
													<input type="radio" id="radio05-1" name="rdo05">
													<label for="radio05-1"  onclick="uploadModule.checkedFileInfo('${uploadFormId}','Y')">공개</label>
													<input type="radio" id="radio05-2" name="rdo05">
													<label for="radio05-2"  onclick="uploadModule.checkedFileInfo('${uploadFormId}','N')">비공개</label>
												</div>
											</div>
										</td>
		 </c:if>
		  -->

</c:if>
 	<div style="clear:both; width:100%; margin-top:8px; font-size:14px; line-height:1.2; color:#5A5A5B;">
        <progress id="progressBar_${uploadFormId}" style="display:none; width:100%" max="100" data-label=""></progress>
	    <span id="progressLabel_${uploadFormId}" style="display:block; line-height:1.2;display:none;"></span>
	    <button  id="download-cancel-btn_${uploadFormId}"   onclick="uploadModule.cancelDownload('${uploadFormId}')" style="display:none;" type="button">취소</button>
	</div>


