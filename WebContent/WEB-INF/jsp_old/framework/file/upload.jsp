<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/framework/_includes/includeTags.jspf" %>



<c:if test="${param.mode eq 'view'}">

	<c:set var="fileClass" value="file-list" />
	<c:if test="${param.viewType eq 'H'}">
		<c:set var="fileClass" value="file-list result" />
	</c:if>
	<c:if test="${param.viewType eq 'V'}">
		<c:set var="fileClass" value="file-list" />
	</c:if>

	<div class="filebox">
		<div data-file-list  data-doc-id="${param.docId}"  data-upload-form-id="${uploadFormId}"  data-mode="${param.mode}"  data-ref-doc-id="${param.refDocId}" data-base-path="${basePath}" >
			<progress id="progressBar_${uploadFormId}"   style="display:none"  max="100" data-label=""></progress>
			<ul id="fileList_${uploadFormId}"   data-max-file-size="${param.maxFileSize}"  data-accept-file="${accept}"  data-single-file="${param.singleFile}"  data-max-file-cnt="${param.maxFileCnt}" class="${fileClass}">
			</ul>
		</div>
		   <label id="progressLabel_${uploadFormId}" <c:if test="${param.mode eq 'view'}">style="display:none" </c:if> ></label>
		   <input type="hidden" name="fileInfoList"  id="fileInfoList_${uploadFormId}" value='{"refDocId":"NONE", "attcDocId":"","docId":"","fileInfo":[]}'>
		  	<c:if test="${not empty param.refDocId}">
		   		<input type="hidden" name="${param.refDocId}" id="docId_${uploadFormId}" value="${param.docId}">
	  	 	</c:if>
	  	 	<input type="hidden" name="refDocId"  id="refDocId_${uploadFormId}" value="NONE">
	</div>
</c:if>

<c:if test="${param.mode ne 'view'}">
	<div class="filebox">
		<div class="btn-add-delete" style="margin-bottom: 10px;">

		<c:if test="${param.noButton ne 'Y'}">
		 	<label class="add blue"  for="file_input_${uploadFormId}" data-file-add><i class="folder-plus" ></i>추가</label>
			<button class="delete" type="button"  onClick="uploadModule.deleteFile('${uploadFormId}')">
				<i class="folder-minus"></i>삭제
			</button>
			<c:if test="${param.arrButton eq 'Y'}">
				<button class="arr" type="button" onClick="uploadModule.sort('${uploadFormId}','up')" ><i class="arr-up"></i></button>
	            <button class="arr" type="button"  onClick="uploadModule.sort('${uploadFormId}','down')"><i class="arr-down"></i></button>
            </c:if>
		</c:if>

		<c:if test="${param.hwpViewer eq 'Y'}">
				<button class="delete" type="button"  onClick="uploadModule.showFile('${uploadFormId}')">
						<span class="btn-blue">조회</span>
				</button>
		</c:if>
		
		<c:if test="${param.cntViewer eq 'Y'}">
			<button class="delete" type="button"  onClick="uploadModule.readFile('${uploadFormId}')">
				<span class="btn-blue">내용조회</span>
			</button>
		</c:if>

			<input type="file"  id="file_input_${uploadFormId}" accept="${accept}" multiple  onChange="uploadModule.upload('${uploadFormId}');" style="display:none"/>
		</div>
		<div data-file-list  data-doc-id="${param.docId}"  data-upload-form-id="${uploadFormId}"  data-mode="${param.mode}"  data-ref-doc-id="${param.refDocId}" data-base-path="${basePath}"   data-no-button="${param.noButton}">
			<progress id="progressBar_${uploadFormId}"   style="display:none"  max="100" data-label=""></progress>
			<ul id="fileList_${uploadFormId}"    data-required-attach-index="${param.requiredAttachIndex}"  data-max-file-size="${param.maxFileSize}"  data-accept-file="${accept}"  data-single-file="${param.singleFile}"  data-max-file-cnt="${param.maxFileCnt}" class="file-list dragndrop">
				<li id="fileList_img_${uploadFormId}" class="none">
                    <img alt="" src="${basePath}/img/cloud-upload-alt-solid.svg">
                   <!--   <span class="desc">Drag &amp; Drop Files</span>-->
                </li>
			</ul>
		</div>
	   <label class="left" id="progressLabel_${uploadFormId}" <c:if test="${param.mode eq 'view'}">style="display:none" </c:if> ></label>
	   <input type="hidden" name="fileInfoList"  id="fileInfoList_${uploadFormId}" value='{"refDocId":"${param.refDocId}", "attcDocId":"", "docId":"", "fileInfo":[]}'>
	   <input type="hidden" name="${param.refDocId}" id="docId_${uploadFormId}" value="${param.docId}">
	   <input type="hidden" name="refDocId"  id="refDocId_${uploadFormId}" value="${param.refDocId}">
	   <c:if test="${not empty param.requiredAttachIndex}">
	   	<input type="hidden" name="${param.refDocId}_${param.requiredAttachIndex}" id="${param.refDocId}_${param.requiredAttachIndex}" value="">
	   </c:if>
	</div>
</c:if>


