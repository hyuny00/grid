<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>
 

<c:if test="${param.isOnlyZip eq 'Y'}">
    <c:set var="accept" value=".zip" />
</c:if>

<c:if test="${param.mode eq 'view'}">

    <div class="file">
        <div class="file-upload" data-file-list data-doc-id="${param.docId}" data-upload-form-id="${uploadFormId}" data-mode="${param.mode}" data-ref-doc-id="${param.refDocId}" data-base-path="${basePath}">
            <ul id="fileList_${uploadFormId}" data-max-file-size="${param.maxFileSize}" data-accept-file="${accept}" data-single-file="${param.singleFile}" data-max-file-cnt="${param.maxFileCnt}" ">
            </ul>
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
        <div class="file-upload" data-file-list data-doc-id="${param.docId}" data-upload-form-id="${uploadFormId}" data-mode="${param.mode}" data-ref-doc-id="${param.refDocId}" data-base-path="${basePath}" data-no-button="${param.noButton}">
            
            <!-- 파일이 없을 때 표시될 메시지 -->
           <p id="emptyMessage_${uploadFormId}" class="txt-file" >첨부하실 파일을 마우스로 끌어오세요.</p>
            
           <ul id="fileList_${uploadFormId}" data-required-attach-index="${param.requiredAttachIndex}" data-max-file-size="${param.maxFileSize}" data-accept-file="${accept}" data-single-file="${param.singleFile}" data-max-file-cnt="${param.maxFileCnt}" class="file-list dragndrop" style="display:none;">
</ul>
            
            <input type="file" id="file_input_${uploadFormId}" accept="${accept}" multiple onChange="uploadModule.upload('${uploadFormId}');" hidden/>
        </div>
        
        <c:if test="${param.noButton ne 'Y'}">
        <div class="btn-wrap">
            <button type="button" class="btn-plus" onclick="document.getElementById('file_input_${uploadFormId}').click();">
                <span class="sr-only">추가</span>
            </button>
            <button type="button" class="btn-del" onclick="uploadModule.deleteFile('${uploadFormId}')">
                <span class="sr-only">삭제</span>
            </button>
            <c:if test="${param.arrButton eq 'Y'}">
                <button type="button" class="btn-arr" onclick="uploadModule.sort('${uploadFormId}','up')">
                    <span class="sr-only">위로</span>
                </button>
                <button type="button" class="btn-arr" onclick="uploadModule.sort('${uploadFormId}','down')">
                    <span class="sr-only">아래로</span>
                </button>
            </c:if>
        </div>
        </c:if>
        
     
       
        <input type="hidden" name="fileInfoList" id="fileInfoList_${uploadFormId}" value='{"refDocId":"${param.refDocId}", "attcDocId":"", "docId":"", "fileInfo":[]}'>
        <input type="hidden" name="${param.refDocId}" id="docId_${uploadFormId}" value="${param.docId}">
        <input type="hidden" name="refDocId" id="refDocId_${uploadFormId}" value="${param.refDocId}">
        <c:if test="${not empty param.requiredAttachIndex}">
            <input type="hidden" name="${param.refDocId}_${param.requiredAttachIndex}" id="${param.refDocId}_${param.requiredAttachIndex}" value="">
        </c:if>
    </div>
    
    <div style="clear:both; width:100%;">
        <progress id="progressBar_${uploadFormId}" style="display:none; width:100%" max="100" data-label=""></progress>
	    <label id="progressLabel_${uploadFormId}" style="display:block; <c:if test="${param.mode eq 'view'}">display:none;</c:if>"></label>
	</div>
</c:if>