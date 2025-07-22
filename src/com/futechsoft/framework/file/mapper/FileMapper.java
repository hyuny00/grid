package com.futechsoft.framework.file.mapper;

import java.util.List;

import com.futechsoft.framework.annotation.Mapper;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.FtMap;

@Mapper("framework.file.mapper.FileMapper")
public interface FileMapper  {

	FileInfoVo selectFileInfo(FtMap params) throws Exception;

	List<FtMap> selectFileList(FtMap params) throws Exception;


	void updateMergeDocId(FtMap params)throws Exception;

	void updateThumbNnailFileInfo(FileInfoVo fileInfoVo)throws Exception;



	FtMap selectFileDataInfo(FtMap params) throws Exception;

	FtMap selectFile(FtMap params) throws Exception;
	
	String selectDocId(FtMap params) throws Exception;
	
	void deleteFile(FtMap params)throws Exception;
	void updateFileOrd(FtMap params)throws Exception;
	
	void insertFileGroup(FtMap params)throws Exception;
	void insertFileInfo(FtMap params)throws Exception;
	
	void deleteDoc(FtMap params)throws Exception;
	
	
	void updateFileGroupDelYn(String docId)throws Exception;
	void updateFileGroupDtDelYn(String docId)throws Exception;
	void updateFileDtDelYn(FtMap params)throws Exception;
}


