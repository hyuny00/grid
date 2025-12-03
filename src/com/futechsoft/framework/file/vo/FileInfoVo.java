package com.futechsoft.framework.file.vo;

import java.io.Serializable;

public class FileInfoVo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String docId;
	private long fileSize;
	private String fileExt;
	private String fileId;
	private String filePath;
	private String tempFilePath;
	private String fileNm;
	private int fileOrd;

	private long totalChunks;
	private long chunkIndex;

	private String uploadComplete;
	private String temp;

	private String delYn = "N";

	private String fileAuth ="rwr-r-";
	private String fileMsg ="";
	private String tblNm;
	private String tblColNm;

	private String thumbnailYn;

	private String fileType;

	private String isOnlyZip;

	private String filePstnSecd;

	private String hmpgRlsYn;

	private String iipsRlsYn;

	private String taskSecd;

	private String extractZipYn;


	public FileInfoVo() {
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileNm() {
		return fileNm;
	}

	public void setFileNm(String fileNm) {
		this.fileNm = fileNm;
	}

	public int getFileOrd() {
		return fileOrd;
	}

	public void setFileOrd(int fileOrd) {
		this.fileOrd = fileOrd;
	}

	public long getTotalChunks() {
		return totalChunks;
	}

	public void setTotalChunks(long totalChunks) {
		this.totalChunks = totalChunks;
	}

	public long getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(long chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	public String getUploadComplete() {
		return uploadComplete;
	}

	public void setUploadComplete(String uploadComplete) {
		this.uploadComplete = uploadComplete;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getDelYn() {
		return delYn;
	}

	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}

	public String getTempFilePath() {
		return tempFilePath;
	}

	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}

	public String getFileAuth() {
		return fileAuth;
	}

	public void setFileAuth(String fileAuth) {
		this.fileAuth = fileAuth;
	}



	public String getFileMsg() {
		return fileMsg;
	}

	public void setFileMsg(String fileMsg) {
		this.fileMsg = fileMsg;
	}

	public String getTblNm() {
		return tblNm;
	}

	public void setTblNm(String tblNm) {
		this.tblNm = tblNm;
	}

	public String getTblColNm() {
		return tblColNm;
	}

	public void setTblColNm(String tblColNm) {
		this.tblColNm = tblColNm;
	}

	public String getThumbnailYn() {
		return thumbnailYn;
	}

	public void setThumbnailYn(String thumbnailYn) {
		this.thumbnailYn = thumbnailYn;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}



	public String getIsOnlyZip() {
		return isOnlyZip;
	}

	public void setIsOnlyZip(String isOnlyZip) {
		this.isOnlyZip = isOnlyZip;
	}

	public String getFilePstnSecd() {
		return filePstnSecd;
	}

	public void setFilePstnSecd(String filePstnSecd) {
		this.filePstnSecd = filePstnSecd;
	}

	public String getHmpgRlsYn() {
		return hmpgRlsYn;
	}

	public void setHmpgRlsYn(String hmpgRlsYn) {
		this.hmpgRlsYn = hmpgRlsYn;
	}

	public String getIipsRlsYn() {
		return iipsRlsYn;
	}

	public void setIipsRlsYn(String iipsRlsYn) {
		this.iipsRlsYn = iipsRlsYn;
	}

	public String getTaskSecd() {
		return taskSecd;
	}

	public void setTaskSecd(String taskSecd) {
		this.taskSecd = taskSecd;
	}

	public String getExtractZipYn() {
		return extractZipYn;
	}

	public void setExtractZipYn(String extractZipYn) {
		this.extractZipYn = extractZipYn;
	}

}
